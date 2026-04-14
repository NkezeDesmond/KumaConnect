package com.nkezedesmond.kumaconnect.p2p.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.nkezedesmond.kumaconnect.p2p.wifi.SendReceiveThread
import java.io.IOException
import java.util.UUID

class BluetoothServerThread(
    private val adapter: BluetoothAdapter, 
    private val handler: Handler,
    private val onStreamReady: (SendReceiveThread) -> Unit
) : Thread() {

    private val APP_NAME = "KumaConnect"
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SPP UUID
    
    @SuppressLint("MissingPermission")
    private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        adapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID)
    }

    override fun run() {
        var socket: BluetoothSocket? = null
        
        while (true) {
            try {
                socket = serverSocket?.accept()
            } catch (e: IOException) {
                break
            }

            if (socket != null) {
                // Connection accepted, map to our unified SendReceiveThread
                val sendReceiveThread = SendReceiveThread(socket.remoteDevice.name, socket.inputStream, socket.outputStream, handler)
                sendReceiveThread.start()
                onStreamReady(sendReceiveThread)
                
                try {
                    serverSocket?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                break
            }
        }
    }
}
