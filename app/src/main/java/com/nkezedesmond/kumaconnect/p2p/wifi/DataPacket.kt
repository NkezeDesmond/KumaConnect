package com.nkezedesmond.kumaconnect.p2p.wifi

/**
 * An envelope protocol mapped to JSON to ensure KumaConnect 
 * can distinguish between a standard text message and an incoming binary file stream.
 */
data class DataPacket(
    val type: DataType,
    val payload: String,      // For TEXT: the actual message. For FILE: the filename.
    val senderId: String,
    val timestamp: Long,
    val byteSize: Long = 0L   // If it's a file, the size of incoming byte stream
)

enum class DataType {
    TEXT,
    FILE_START,
    FILE_CHUNK,
    FILE_END,
    SYNC_HISTORY
}
