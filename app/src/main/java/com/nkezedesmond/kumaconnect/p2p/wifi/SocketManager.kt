package com.nkezedesmond.kumaconnect.p2p.wifi

/**
 * A central Singleton to hold the active peer-to-peer socket stream.
 * This allows the ChatActivity to simply access the global connection without 
 * complex Intent Parcelable byte-stream mapping.
 */
object SocketManager {
    var activeThread: SendReceiveThread? = null

    // Sends the byte packet over whoever is currently connected
    fun sendData(data: ByteArray) {
        activeThread?.write(data)
    }
}
