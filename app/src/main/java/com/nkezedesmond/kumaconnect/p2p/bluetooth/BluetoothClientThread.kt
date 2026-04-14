package com.nkezedesmond.kumaconnect.p2p.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.nkezedesmond.kumaconnect.p2p.wifi.SendReceiveThread
import java.io.IOException
import java.util.UUID

class BluetoothClientThread(
    private val device: BluetoothDevice, 
    private val handler: Handler,
    private val onStreamReady: (SendReceiveThread) -> Unit
) : Thread() {

    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    private val socket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(MY_UUID)
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        try {
            socket?.connect()
        } catch (connectException: IOException) {
            try {
                socket?.close()
            } catch (closeException: IOException) { }
            return
        }

        socket?.let {
            // Connection successful! Pass to our unified SendReceiveThread
            val sendReceiveThread = SendReceiveThread(device.name, it.inputStream, it.outputStream, handler)
            sendReceiveThread.start()
            onStreamReady(sendReceiveThread)
        }
    }
}
