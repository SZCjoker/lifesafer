package com.lifesafer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.lifesafer.network.UdpBroadcastHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationText: TextView
    private lateinit var messageText: TextView
    private lateinit var sendButton: Button
    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationText = findViewById(R.id.locationText)
        messageText = findViewById(R.id.messageText)
        sendButton = findViewById(R.id.sendButton)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()
        UdpBroadcastHelper.startListening { message, address ->
            runOnUiThread {
                messageText.text = "From ${address.hostAddress}: $message"
            }
        }
        sendButton.setOnClickListener {
            val loc = lastLocation
            if (loc != null) {
                val msg = "Device@${loc.latitude},${loc.longitude}"
                UdpBroadcastHelper.sendBroadcast(msg)
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLocation()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocation()
        } else {
            locationText.text = "Location permission denied"
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                lastLocation = location
                if (location != null) {
                    locationText.text = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                } else {
                    locationText.text = "Location unavailable"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UdpBroadcastHelper.stopListening()
    }
}
