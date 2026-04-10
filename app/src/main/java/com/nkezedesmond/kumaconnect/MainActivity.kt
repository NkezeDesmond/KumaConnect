package com.nkezedesmond.kumaconnect

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nkezedesmond.kumaconnect.p2p.wifi.WifiDirectManager

class MainActivity : AppCompatActivity() {

    private lateinit var wifiDirectManager: WifiDirectManager
    private val peers = mutableListOf<WifiP2pDevice>()

    // View references
    private lateinit var btnDiscover: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDiscover = findViewById(R.id.btnDiscover)
        tvStatus = findViewById(R.id.tvStatus)

        wifiDirectManager = WifiDirectManager(this, this)

        requestPermissions()

        btnDiscover.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                wifiDirectManager.manager?.discoverPeers(wifiDirectManager.channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        tvStatus.text = "Discovery Started..."
                    }

                    override fun onFailure(reasonCode: Int) {
                        tvStatus.text = "Discovery Failed: $reasonCode"
                    }
                })
            } else {
                Toast.makeText(this, "Location permission required for discovery", Toast.LENGTH_SHORT).show()
                requestPermissions()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        wifiDirectManager.registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        wifiDirectManager.unregisterReceiver()
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        val ungranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (ungranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, ungranted.toTypedArray(), 100)
        }
    }

    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
            
            // TODO: Update RecyclerView Adapter here
            if (peers.isEmpty()) {
                tvStatus.text = "No devices found."
            } else {
                tvStatus.text = "Found ${peers.size} devices"
            }
        }
    }
}
