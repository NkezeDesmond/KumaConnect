package com.nkezedesmond.kumaconnect.core.network

/**
 * Manages the connectivity state of the application.
 * Determines whether the device is offline, using Wi-Fi Direct, Bluetooth, 
 * or has internet access (for AI summarization features).
 */
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.nkezedesmond.kumaconnect.ai.summarizer.GeminiSummarizerService

class NetworkStateManager(private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("NetworkState", "Internet connection newly available. Triggering AI Sync...")
                // Trigger AI Summarization Service now that we have internet
                GeminiSummarizerService.checkAndSummarizeMessages(context)
            }

            override fun onLost(network: Network) {
                Log.d("NetworkState", "Internet connection lost. Switching to purely offline logic.")
            }
        })
    }
}
