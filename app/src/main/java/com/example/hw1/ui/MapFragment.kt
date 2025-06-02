package com.example.hw1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hw1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val defaultLocation = LatLng(32.0853, 34.7818)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
    }

    fun zoomToLocation(lat: Double, lon: Double) {
        if (::googleMap.isInitialized) {
            val location = LatLng(lat,lon)
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(location).title("Score Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

}
