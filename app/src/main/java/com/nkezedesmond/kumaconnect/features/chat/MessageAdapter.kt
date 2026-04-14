package com.nkezedesmond.kumaconnect.features.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nkezedesmond.kumaconnect.R
import com.nkezedesmond.kumaconnect.core.database.entities.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private var messages: List<Message>, private val currentUserId: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = if (viewType == VIEW_TYPE_SENT) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
        }
        return MessageViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(itemView: View, private val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(if (viewType == VIEW_TYPE_SENT) R.id.tvSentMessage else R.id.tvReceivedMessage)
        private val tvTime: TextView = itemView.findViewById(if (viewType == VIEW_TYPE_SENT) R.id.tvSentTime else R.id.tvReceivedTime)

        fun bind(message: Message) {
            tvMessage.text = message.text
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvTime.text = sdf.format(Date(message.timestamp))
        }
    }
}
