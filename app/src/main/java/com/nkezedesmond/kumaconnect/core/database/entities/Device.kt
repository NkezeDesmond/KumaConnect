package com.nkezedesmond.kumaconnect.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey val macAddress: String, // Unique hardware address for P2P connection
    val deviceName: String,
    val lastSeenTimestamp: Long
)
