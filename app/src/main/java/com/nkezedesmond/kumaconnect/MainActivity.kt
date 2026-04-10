package com.nkezedesmond.kumaconnect

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nkezedesmond.kumaconnect.features.chat.PeerListAdapter
import com.nkezedesmond.kumaconnect.p2p.wifi.ClientClass
import com.nkezedesmond.kumaconnect.p2p.wifi.SendReceiveThread
import com.nkezedesmond.kumaconnect.p2p.wifi.ServerClass
import com.nkezedesmond.kumaconnect.p2p.wifi.WifiDirectManager

class MainActivity : AppCompatActivity() {

    private lateinit var wifiDirectManager: WifiDirectManager
    private val peers = mutableListOf<WifiP2pDevice>()
    private lateinit var peerListAdapter: PeerListAdapter

    // Network & Streams
    var sendReceiveThread: SendReceiveThread? = null

    // View references
    private lateinit var btnDiscover: Button
    private lateinit var tvStatus: TextView
    private lateinit var rvPeers: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDiscover = findViewById(R.id.btnDiscover)
        tvStatus = findViewById(R.id.tvStatus)
        rvPeers = findViewById(R.id.rvPeers)

        wifiDirectManager = WifiDirectManager(this, this)
        
        setupRecyclerView()
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
    
    private fun setupRecyclerView() {
        peerListAdapter = PeerListAdapter(peers) { device ->
            connectToPeer(device)
        }
        rvPeers.layoutManager = LinearLayoutManager(this)
        rvPeers.adapter = peerListAdapter
    }

    @SuppressLint("MissingPermission")
    private fun connectToPeer(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }
        wifiDirectManager.manager?.connect(wifiDirectManager.channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "Connected to ${device.deviceName}", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Connection Failed. Retry.", Toast.LENGTH_SHORT).show()
            }
        })
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

    // Handles Peer List UI updates
    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        if (peerList.deviceList != peers) {
            peers.clear()
            peers.addAll(peerList.deviceList)
            peerListAdapter.notifyDataSetChanged()
            
            if (peers.isEmpty()) {
                tvStatus.text = "No devices found."
            } else {
                tvStatus.text = "Found ${peers.size} devices"
            }
        }
    }

    // Handles Connection Info - Detects if we are Group Owner (Server) or Client
    val connectionInfoListener = WifiP2pManager.ConnectionInfoListener { info ->
        val groupOwnerAddress = info.groupOwnerAddress

        if (info.groupFormed && info.isGroupOwner) {
            tvStatus.text = "Host (Server)"
            ServerClass(handler) { stream -> sendReceiveThread = stream }.start()
        } else if (info.groupFormed) {
            tvStatus.text = "Client"
            ClientClass(groupOwnerAddress, handler) { stream -> sendReceiveThread = stream }.start()
        }
    }

    // Handler to process messages coming from SendReceiveThread directly to the UI
    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            SendReceiveThread.MESSAGE_READ -> {
                val readBuffer = msg.obj as ByteArray
                val receivedMessage = String(readBuffer, 0, msg.arg1)
                // TODO: Save message to Room Database and show in Chat UI
                Toast.makeText(this, "New msg: $receivedMessage", Toast.LENGTH_LONG).show()
            }
        }
        true
    }
}
