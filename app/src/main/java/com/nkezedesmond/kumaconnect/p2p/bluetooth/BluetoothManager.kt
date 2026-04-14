package com.nkezedesmond.kumaconnect.p2p.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.widget.Toast

class BluetoothManager(private val context: Context) {
    val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    fun isBluetoothSupported(): Boolean {
        return adapter != null
    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth() {
        if (adapter?.isEnabled == false) {
            Toast.makeText(context, "Please enable Bluetooth in settings", Toast.LENGTH_SHORT).show()
        }
    }

    // In a full implementation, you would add BroadcastReceivers here 
    // for ACTION_FOUND to discover devices just like Wi-Fi Direct.
}
