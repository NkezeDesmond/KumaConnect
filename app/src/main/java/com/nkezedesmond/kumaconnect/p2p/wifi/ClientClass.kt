package com.nkezedesmond.kumaconnect.p2p.wifi

import android.os.Handler
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class ClientClass(
    private val hostAddress: InetAddress,
    private val handler: Handler,
    private val onStreamReady: (SendReceiveThread) -> Unit
) : Thread() {

    private val socket = Socket()

    override fun run() {
        try {
            socket.connect(InetSocketAddress(hostAddress, 8888), 5000)
            
            val sendReceiveThread = SendReceiveThread(socket, handler)
            sendReceiveThread.start()
            onStreamReady(sendReceiveThread)
            
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                socket.close()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }
    }
}
