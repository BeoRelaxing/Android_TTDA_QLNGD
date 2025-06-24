package com.example.quanlykhunghiduong.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.ServiceBooking

class ServiceBookingAdapter(
    private var items: List<ServiceBooking>,
    private val onItemClick: (ServiceBooking) -> Unit
) : RecyclerView.Adapter<ServiceBookingAdapter.ViewHolder>() {

    private var selectedId: Int? = null

    fun updateData(newItems: List<ServiceBooking>) {
        items = newItems
        selectedId = null
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: LinearLayout = itemView.findViewById(R.id.itemServiceBookingRoot)
        val tvServiceBookingId: TextView = itemView.findViewById(R.id.tvServiceBookingId)
        val tvServiceId: TextView = itemView.findViewById(R.id.tvServiceId)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvServiceBookingId.text = item.service_booking_id.toString()
        holder.tvServiceId.text = item.service_id.toString()
        holder.tvQuantity.text = item.quantity.toString()
        holder.tvTotalPrice.text = item.total_price.toString()

        if (item.service_booking_id == selectedId) {
            holder.root.setBackgroundColor(Color.parseColor("#FFDDDD"))
        } else {
            holder.root.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            selectedId = item.service_booking_id
            notifyDataSetChanged()
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
} 