package com.example.hw1.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.hw1.interfaces.TiltCallback

class TiltDetector(context: Context, private var tiltCallback: TiltCallback?) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
    private lateinit var sensorEventListener: SensorEventListener

    private var timestamp: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                handleTiltX(x)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    private fun handleTiltX(x: Float) {
        if (System.currentTimeMillis() - timestamp >= 100) {
            timestamp = System.currentTimeMillis()
            if (x > 2.5f) {
                tiltCallback?.tiltLeft()
            } else if (x < -2.5f) {
                tiltCallback?.tiltRight()
            }
        }
    }

    fun start() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}