package com.nkezedesmond.kumaconnect.p2p.wifi

import android.os.Handler
import java.io.IOException
import java.net.ServerSocket

class ServerClass(private val handler: Handler, private val onStreamReady: (SendReceiveThread) -> Unit) : Thread() {
    
    private var serverSocket: ServerSocket? = null

    override fun run() {
        try {
            serverSocket = ServerSocket(8888)
            val socket = serverSocket!!.accept()
            
            // Reached when a client successfully connects to this node
            val sendReceiveThread = SendReceiveThread(socket, handler)
            sendReceiveThread.start()
            onStreamReady(sendReceiveThread)
            
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
