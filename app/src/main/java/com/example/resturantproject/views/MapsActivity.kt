package com.example.resturantproject.views

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.R
import com.example.resturantproject.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var polylines: ArrayList<PolylineOptions>
    private lateinit var camera: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        polylines = intent.getParcelableArrayListExtra("polylines") ?: ArrayList()
        camera = intent.getParcelableExtra("camera") ?: LatLng(0.0, 0.0)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        for (pl in polylines) {
            mMap.addPolyline(pl)
            for (p in pl.points) {
                mMap.addMarker(MarkerOptions().position(p))
            }
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 15f))
    }
}