package com.nkezedesmond.kumaconnect.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nkezedesmond.kumaconnect.core.database.entities.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): List<Message>

    @Query("SELECT * FROM messages WHERE senderId = :senderId ORDER BY timestamp ASC")
    fun getMessagesBySender(senderId: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)
}
