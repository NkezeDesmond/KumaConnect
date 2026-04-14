package com.nkezedesmond.kumaconnect.features.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nkezedesmond.kumaconnect.R
import com.nkezedesmond.kumaconnect.core.database.entities.Message
import com.nkezedesmond.kumaconnect.p2p.wifi.DataPacket
import com.nkezedesmond.kumaconnect.p2p.wifi.DataType
import com.nkezedesmond.kumaconnect.p2p.wifi.SendReceiveThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.widget.Toast
import com.nkezedesmond.kumaconnect.p2p.wifi.SocketManager

class ChatActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var messageAdapter: MessageAdapter

    private val currentUserId = "ME"
    private val peerId = "PEER"

    private val db by lazy { com.nkezedesmond.kumaconnect.core.database.AppDatabase.getDatabase(this) }
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Visual Handshake Indicator
        Toast.makeText(this, "🤝 Handshake Successful! Connection Secured for sharing.", Toast.LENGTH_LONG).show()

        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        messageAdapter = MessageAdapter(emptyList(), currentUserId)
        rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvMessages.adapter = messageAdapter

        loadChatHistory()

        val isGroupOwner = intent.getBooleanExtra("IS_GROUP_OWNER", false)
        if (isGroupOwner) {
            pushHistoryToNewPeers()
        }

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
            }
        }
    }

    private fun pushHistoryToNewPeers() {
        CoroutineScope(Dispatchers.IO).launch {
            val allMessages = db.messageDao().getAllMessages()
            if (allMessages.isNotEmpty()) {
                val arrayJson = gson.toJson(allMessages)
                val syncPacket = DataPacket(DataType.SYNC_HISTORY, arrayJson, currentUserId, System.currentTimeMillis())
                SocketManager.sendData(gson.toJson(syncPacket).toByteArray())
            }
        }
    }

    private fun loadChatHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val messages = db.messageDao().getAllMessages()
            withContext(Dispatchers.Main) {
                messageAdapter.updateMessages(messages)
                if (messages.isNotEmpty()) {
                    rvMessages.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

    private fun sendMessage(text: String) {
        val timestamp = System.currentTimeMillis()
        val message = Message(text = text, senderId = currentUserId, timestamp = timestamp)

        CoroutineScope(Dispatchers.IO).launch {
            db.messageDao().insertMessage(message)
            loadChatHistory()
        }

        val packet = DataPacket(DataType.TEXT, text, currentUserId, timestamp)
        val jsonPayload = gson.toJson(packet)
        
        SocketManager.sendData(jsonPayload.toByteArray())
        etMessage.text.clear()
    }

    val chatHandler = Handler(Looper.getMainLooper()) { msg ->
        if (msg.what == SendReceiveThread.MESSAGE_READ) {
            val readBuffer = msg.obj as ByteArray
            val jsonString = String(readBuffer, 0, msg.arg1)

            try {
                val packet = gson.fromJson(jsonString, DataPacket::class.java)
                
                if (packet.type == DataType.TEXT) {
                    val receivedMessage = Message(
                        text = packet.payload,
                        senderId = packet.senderId,
                        timestamp = packet.timestamp
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.messageDao().insertMessage(receivedMessage)
                        loadChatHistory()
                    }
                } else if (packet.type == DataType.SYNC_HISTORY) {
                    // New client receives whole DB history from Group Owner
                    val historyArray = gson.fromJson(packet.payload, Array<Message>::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        historyArray.forEach { db.messageDao().insertMessage(it) }
                        loadChatHistory()
                    }
                    Toast.makeText(this@ChatActivity, "Group History Synchronized!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        true
    }
}
