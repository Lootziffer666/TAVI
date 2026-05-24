package com.example.tavi.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class TiltState(val x: Float, val y: Float)

class SpatialSensorManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val alpha = 0.08f
    private var filteredX = 0f
    private var filteredY = 0f

    val tiltFlow: Flow<TiltState> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                filteredX = alpha * event.values[0] + (1 - alpha) * filteredX
                filteredY = alpha * event.values[1] + (1 - alpha) * filteredY
                val normalized = TiltState(
                    x = (filteredX / 9.8f).coerceIn(-1f, 1f),
                    y = (filteredY / 9.8f).coerceIn(-1f, 1f)
                )
                trySend(normalized)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
