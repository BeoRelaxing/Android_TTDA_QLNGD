package com.example.quanlykhunghiduong.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.Notification

class NotificationAdapter(
    private var items: List<Notification>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    fun updateData(newItems: List<Notification>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNotificationTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        val tvNotificationMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        val tvSentAt: TextView = itemView.findViewById(R.id.tvSentAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNotificationTitle.text = item.title
        holder.tvNotificationMessage.text = item.message
        holder.tvSentAt.text = item.sent_at
        holder.itemView.alpha = if (item.is_read) 1.0f else 0.6f
    }

    override fun getItemCount(): Int = items.size
} 