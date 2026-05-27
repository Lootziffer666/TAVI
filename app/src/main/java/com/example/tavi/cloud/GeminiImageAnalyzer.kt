package com.example.tavi.cloud

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class GeminiImageAnalyzer(
    private val service: GeminiApiService,
    private val apiKey: String,
    private val context: Context
) {
    suspend fun analyze(uri: Uri, prompt: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: error("Cannot open image")
            val bitmap = BitmapFactory.decodeStream(inputStream).also { inputStream.close() }
                ?: error("Cannot decode image")
            analyzeInternal(bitmap, prompt)
        }
    }

    suspend fun analyze(bitmap: Bitmap, prompt: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching { analyzeInternal(bitmap, prompt) }
    }

    private suspend fun analyzeInternal(bitmap: Bitmap, prompt: String): String {
        val maxDim = 1024
        val scale = minOf(1f, maxDim.toFloat() / maxOf(bitmap.width, bitmap.height))
        val scaled = if (scale < 1f)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        else bitmap

        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
        val base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)

        val response = service.generateContent(
            apiKey, GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt),
                            GeminiPart(inlineData = GeminiInlineData("image/jpeg", base64))
                        )
                    )
                )
            )
        )
        return response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: error("No analysis result")
    }
}
