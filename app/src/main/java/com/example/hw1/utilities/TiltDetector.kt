package com.example.hw1.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.hw1.interfaces.TiltCallback
import kotlin.math.abs



class TiltDetector(context: Context, private var tiltCallback: TiltCallback?) {

    private val sensorManager = context
        .getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensor = sensorManager
        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor

    private lateinit var sensorEventListener: SensorEventListener

    var tiltCounterX: Int = 0
        private set

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

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
        }
    }

    private fun handleTiltX(x: Float) {
        if (System.currentTimeMillis() - timestamp >= 500) {
            timestamp = System.currentTimeMillis()
            if (abs(x) >= 3.0) {
                tiltCounterX++
                if (x > 0) {
                    tiltCallback?.tiltLeft()
                } else {
                    tiltCallback?.tiltRight()
                }
            }
        }
    }

    fun start() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener, sensor)
    }


}