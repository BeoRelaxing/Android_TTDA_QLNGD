package com.example.quanlykhunghiduong.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.Booking

class BookingAdapter(
    private var items: List<Booking>,
    private val onItemClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    private var selectedBookingId: Int? = null

    fun updateData(newItems: List<Booking>) {
        items = newItems
        selectedBookingId = null
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: LinearLayout = itemView.findViewById(R.id.itemBookingRoot)
        val tvBookingId: TextView = itemView.findViewById(R.id.tvBookingId)
        val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        val tvRoomId: TextView = itemView.findViewById(R.id.tvRoomId)
        val tvCheckIn: TextView = itemView.findViewById(R.id.tvCheckIn)
        val tvCheckOut: TextView = itemView.findViewById(R.id.tvCheckOut)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvBookingId.text = item.booking_id.toString()
        holder.tvUserId.text = item.user_id.toString()
        holder.tvRoomId.text = item.room_id.toString()
        holder.tvCheckIn.text = item.check_in_date
        holder.tvCheckOut.text = item.check_out_date
        holder.tvTotalPrice.text = item.total_price.toString()
        holder.tvStatus.text = item.status
        holder.tvCreatedAt.text = item.created_at

        // Highlight dòng được chọn
        if (item.booking_id == selectedBookingId) {
            holder.root.setBackgroundColor(Color.parseColor("#FFDDDD"))
        } else {
            holder.root.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            val previousId = selectedBookingId
            selectedBookingId = item.booking_id
            notifyDataSetChanged()
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}