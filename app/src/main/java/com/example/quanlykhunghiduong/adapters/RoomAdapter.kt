package com.example.quanlykhunghiduong.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.Room

class RoomAdapter(
    private var items: List<Room>,
    private val onItemClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    fun updateData(newItems: List<Room>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomNumber: TextView = itemView.findViewById(R.id.tvRoomNumber)
        val tvRoomType: TextView = itemView.findViewById(R.id.tvRoomType)
        val tvRoomPrice: TextView = itemView.findViewById(R.id.tvRoomPrice)
        val tvRoomCapacity: TextView = itemView.findViewById(R.id.tvRoomCapacity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvRoomNumber.text = item.room_number
        holder.tvRoomType.text = item.room_type
        holder.tvRoomPrice.text = item.price_per_night.toString()
        holder.tvRoomCapacity.text = item.capacity.toString()
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
} 