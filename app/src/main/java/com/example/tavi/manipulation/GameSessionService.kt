package com.example.tavi.manipulation

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.tavi.BuildConfig
import com.example.tavi.cloud.GeminiImageAnalyzer
import com.example.tavi.cloud.RetrofitClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class GameSessionService : Service() {

    companion object {
        const val EXTRA_RESULT_CODE = "resultCode"
        const val EXTRA_RESULT_DATA = "resultData"
        const val EXTRA_PACKAGE_NAME = "packageName"
        const val EXTRA_APP_LABEL = "appLabel"
        const val EXTRA_INTERVAL_SECONDS = "intervalSeconds"
        const val ACTION_STOP = "com.example.tavi.STOP_SESSION"
        const val CHANNEL_ID = "tavi_game_session"
        const val NOTIF_ID = 9001
        const val ALERT_NOTIF_ID_BASE = 9002

        // Process-lifetime state; ViewModel observes these
        val livePatterns = MutableStateFlow<List<String>>(emptyList())
        val sessionDebrief = MutableStateFlow<SessionDebrief?>(null)

        private var startTime = 0L
        private var currentPackage = ""
        private var currentLabel = ""
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val accumulated = mutableListOf<String>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
            ?: return START_NOT_STICKY
        val resultData: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(EXTRA_RESULT_DATA)
        }
        if (resultData == null) return START_NOT_STICKY

        currentPackage = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        currentLabel = intent.getStringExtra(EXTRA_APP_LABEL) ?: currentPackage
        val interval = intent.getIntExtra(EXTRA_INTERVAL_SECONDS, 60)

        startTime = System.currentTimeMillis()
        accumulated.clear()
        livePatterns.value = emptyList()
        sessionDebrief.value = null

        startForeground(NOTIF_ID, buildForegroundNotification())

        val mp = getSystemService(MediaProjectionManager::class.java)
            .getMediaProjection(resultCode, resultData)
        mediaProjection = mp
        startCapture(mp, interval)

        return START_NOT_STICKY
    }

    private fun startCapture(mp: MediaProjection, intervalSec: Int) {
        val metrics = resources.displayMetrics
        val captureWidth = minOf(metrics.widthPixels, 720)
        val captureHeight = (captureWidth * metrics.heightPixels.toFloat() / metrics.widthPixels).toInt()

        imageReader = ImageReader.newInstance(captureWidth, captureHeight, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mp.createVirtualDisplay(
            "tavi-game-watch", captureWidth, captureHeight, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface, null, null
        )

        val apiKey = BuildConfig.GEMINI_API_KEY
            .takeIf { it.isNotBlank() && it != "placeholder" }
        if (apiKey == null) { stopSelf(); return }

        val analyzer = GeminiImageAnalyzer(RetrofitClient.geminiService, apiKey, this)
        var alertSeq = 0

        scope.launch {
            while (isActive) {
                delay(intervalSec * 1000L)

                val image = runCatching { imageReader?.acquireLatestImage() }.getOrNull() ?: continue
                val bitmap = runCatching {
                    val plane = image.planes[0]
                    val rowPadding = plane.rowStride - plane.pixelStride * image.width
                    val bmp = Bitmap.createBitmap(
                        image.width + rowPadding / plane.pixelStride,
                        image.height,
                        Bitmap.Config.ARGB_8888
                    )
                    bmp.copyPixelsFromBuffer(plane.buffer)
                    bmp
                }.also { image.close() }.getOrNull() ?: continue

                val prompt = """
                    Mobile game/app screenshot. List any psychological manipulation patterns visible in the UI:
                    FOMO countdown, limited-time offer, energy/stamina gate, loot box prompt, pay-to-win screen,
                    streak pressure, social comparison pressure, notification opt-in pressure.
                    One item per line. Reply NONE if nothing is visible.
                """.trimIndent()

                analyzer.analyze(bitmap, prompt).onSuccess { result ->
                    val patterns = result.lines()
                        .map { it.trim() }
                        .filter { it.isNotBlank() && !it.equals("NONE", ignoreCase = true) }
                        .take(5)
                    if (patterns.isNotEmpty()) {
                        accumulated.addAll(patterns)
                        livePatterns.value = accumulated.distinct()
                        postAlertNotification(patterns.first(), ++alertSeq)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        sessionDebrief.value = SessionDebrief(
            packageName = currentPackage,
            appLabel = currentLabel,
            detectedPatterns = accumulated.distinct(),
            durationMinutes = ((System.currentTimeMillis() - startTime) / 60_000).toInt()
        )
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Game Watch",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Active while TAVI watches for manipulation patterns" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildForegroundNotification(): Notification {
        val stopPi = PendingIntent.getService(
            this, 0,
            Intent(this, GameSessionService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Watching: $currentLabel")
            .setContentText("Analyzing for manipulation patterns every session")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPi)
            .setOngoing(true)
            .build()
    }

    private fun postAlertNotification(pattern: String, seq: Int) {
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pattern in $currentLabel")
            .setContentText(pattern)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(true)
            .build()
        getSystemService(NotificationManager::class.java).notify(ALERT_NOTIF_ID_BASE + seq, notif)
    }
}
