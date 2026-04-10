package com.nkezedesmond.kumaconnect.p2p.wifi

import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class SendReceiveThread(private val socket: Socket, private val handler: Handler) : Thread() {

    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    companion object {
        const val MESSAGE_READ = 1
    }

    init {
        try {
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        val buffer = ByteArray(1024)
        var bytes: Int

        // Constantly loop to read incoming bytes offline
        while (socket.isConnected) {
            try {
                inputStream?.let {
                    bytes = it.read(buffer)
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream?.write(bytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
