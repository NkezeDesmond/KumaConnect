package com.nkezedesmond.kumaconnect.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val senderId: String,       // The MAC address or ID of the sender
    val timestamp: Long,        // Time the message was sent
    val isSummary: Boolean = false, // True if this text is an AI-generated summary
    val relatedFileUri: String? = null // URI if this message is a transferred file
)
