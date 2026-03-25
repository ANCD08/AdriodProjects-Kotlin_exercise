package com.example.hydratrack.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.hydratrack.utils.Constants
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var shakeThreshold = Constants.SHAKE_THRESHOLD

    private var firstCrossTimeMs = 0L
    private var lastShakeTimeMs  = 0L
    private var directionChanges = 0
    private var lastAxisSign     = 0

    private var isListening = false

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            isListening = true
        }
    }

    fun stop() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
        resetWindow()
    }

    fun setThreshold(threshold: Float) {
        shakeThreshold = threshold
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val netAccel = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        val now = System.currentTimeMillis()

        if (now - lastShakeTimeMs < Constants.SHAKE_COOLDOWN_MS) return

        if (netAccel > shakeThreshold) {
            if (firstCrossTimeMs == 0L) {
                firstCrossTimeMs = now
                directionChanges = 0
                lastAxisSign     = 0
            }

            val currentSign = if (x >= 0f) 1 else -1
            if (lastAxisSign != 0 && currentSign != lastAxisSign) {
                directionChanges++
            }
            lastAxisSign = currentSign

            val elapsed = now - firstCrossTimeMs
            if (
                elapsed >= Constants.SHAKE_CONFIRMATION_WINDOW_MS &&
                directionChanges >= Constants.SHAKE_MIN_DIRECTION_CHANGES
            ) {
                lastShakeTimeMs = now
                resetWindow()
                onShakeDetected()
            }

        } else {
            if (firstCrossTimeMs != 0L) resetWindow()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun resetWindow() {
        firstCrossTimeMs = 0L
        directionChanges = 0
        lastAxisSign     = 0
    }
}
