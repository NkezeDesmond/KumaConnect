package com.nkezedesmond.kumaconnect.p2p.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager

/**
 * Handles Wi-Fi Direct peer discovery, group ownership, and server/client socket connections.
 * This is a core component for the offline-first messaging aspect of KumaConnect.
 */
class WifiDirectManager(private val context: Context, private val activity: com.nkezedesmond.kumaconnect.MainActivity) {
    
    val manager: WifiP2pManager? by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    }
    
    var channel: WifiP2pManager.Channel? = null
    var receiver: WifiDirectBroadcastReceiver? = null
    
    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    init {
        channel = manager?.initialize(context, context.mainLooper, null)
        channel?.let {
            receiver = WifiDirectBroadcastReceiver(manager!!, it, activity)
        }
    }

    fun registerReceiver() {
        receiver?.let { context.registerReceiver(it, intentFilter) }
    }

    fun unregisterReceiver() {
        receiver?.let { context.unregisterReceiver(it) }
    }
}
