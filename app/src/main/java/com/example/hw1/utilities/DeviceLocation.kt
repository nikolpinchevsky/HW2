package com.example.hw1.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class DeviceLocation private constructor(private val context: Context) {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private var instance: DeviceLocation? = null
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback

        @Volatile private var lat: Double = 0.0
        @Volatile private var lng: Double = 0.0

        fun init(context: Context) {
            if (instance == null) {
                instance = DeviceLocation(context)
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    1000
                ).build()

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        for (location in result.locations) {
                            lat = location.latitude
                            lng = location.longitude
                        }
                    }
                }
            }
        }

        fun getInstance(): DeviceLocation {
            return instance ?: throw IllegalStateException("DeviceLocation not initialized. Call init(context) first.")
        }

        fun getLat(): Double = lat
        fun getLng(): Double = lng
    }

    fun startLocationUpdates(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission(activity)
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

}