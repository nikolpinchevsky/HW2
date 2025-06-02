package com.example.hw1

import android.app.Application
import com.example.hw1.utilities.DeviceLocation
import com.example.hw1.utilities.ImageLoader

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)
        DeviceLocation.init(this)
    }

}