package com.example.quanlykhunghiduong.CustomerActivity

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R

class CustomerRoomAdapter(
    private var items: List<CustomerRoom>,
    private val onItemClick: (CustomerRoom) -> Unit
) : RecyclerView.Adapter<CustomerRoomAdapter.ViewHolder>() {
    private var selectedRoomId: Int? = null

    fun updateData(newItems: List<CustomerRoom>) {
        items = newItems
        selectedRoomId = null
        notifyDataSetChanged()
    }

    fun setSelectedRoom(roomId: Int?) {
        selectedRoomId = roomId
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomNumber: TextView = itemView.findViewById(R.id.tvRoomNumber)
        val tvRoomType: TextView = itemView.findViewById(R.id.tvRoomType)
        val tvRoomPrice: TextView = itemView.findViewById(R.id.tvRoomPrice)
        val tvRoomStatus: TextView = itemView.findViewById(R.id.tvRoomStatus)
        val tvRoomCapacity: TextView = itemView.findViewById(R.id.tvRoomCapacity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_room, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvRoomNumber.text = item.room_number
        holder.tvRoomType.text = item.room_type
        holder.tvRoomPrice.text = item.price_per_night.toString()
        holder.tvRoomStatus.text = item.status
        holder.tvRoomCapacity.text = item.capacity.toString()

        if (item.room_id == selectedRoomId) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFDDDD"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            setSelectedRoom(item.room_id)
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
} 