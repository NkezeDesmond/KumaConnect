package com.nkezedesmond.kumaconnect.features.chat

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nkezedesmond.kumaconnect.R

class PeerListAdapter(
    private val deviceList: List<WifiP2pDevice>,
    private val onDeviceClicked: (WifiP2pDevice) -> Unit
) : RecyclerView.Adapter<PeerListAdapter.PeerViewHolder>() {

    class PeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDeviceName: TextView = itemView.findViewById(R.id.tvDeviceName)
        val tvDeviceMac: TextView = itemView.findViewById(R.id.tvDeviceMac)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_peer, parent, false)
        return PeerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeerViewHolder, position: Int) {
        val device = deviceList[position]
        holder.tvDeviceName.text = device.deviceName
        holder.tvDeviceMac.text = device.deviceAddress
        holder.itemView.setOnClickListener {
            onDeviceClicked(device)
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }
}
