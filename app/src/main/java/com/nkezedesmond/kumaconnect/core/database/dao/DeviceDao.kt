package com.nkezedesmond.kumaconnect.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nkezedesmond.kumaconnect.core.database.entities.Device

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY lastSeenTimestamp DESC")
    fun getAllDevices(): List<Device>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(device: Device)
}
