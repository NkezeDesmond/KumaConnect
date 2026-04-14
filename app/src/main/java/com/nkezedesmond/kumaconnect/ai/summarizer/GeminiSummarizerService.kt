package com.nkezedesmond.kumaconnect.ai.summarizer

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object GeminiSummarizerService {

    private val TAG = "GeminiSummarizer"

    fun checkAndSummarizeMessages(context: Context) {
        val db = com.nkezedesmond.kumaconnect.core.database.AppDatabase.getDatabase(context)

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Internet detected. Scanning for huge bodies of text to summarize...")
            
            val allMessages = db.messageDao().getAllMessages()
            val unsummarized = allMessages.filter { !it.isSummary && it.text.length > 50 }

            if (unsummarized.isNotEmpty()) {
                Log.d(TAG, "Found ${unsummarized.size} large messages. Sending to AI API (Mock)...")
                
                // Simulate network latency to Google Gemini API
                delay(2000) 
                
                for (msg in unsummarized) {
                    val summaryText = "[AI Summary]: " + msg.text.take(30) + "..."
                    val updatedMsg = msg.copy(
                        isSummary = true,
                        text = summaryText
                    )
                    db.messageDao().insertMessage(updatedMsg)
                    Log.d(TAG, "Summarized text saved to Offline Room DB.")
                }
            } else {
                Log.d(TAG, "No massive offline messages found requiring AI intervention.")
            }
        }
    }
}
