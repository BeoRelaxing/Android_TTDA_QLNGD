package com.example.quanlykhunghiduong.CustomerActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.Resort

class CustomerResortAdapter(
    private var items: List<Resort>,
    private val onItemClick: (Resort) -> Unit
) : RecyclerView.Adapter<CustomerResortAdapter.ViewHolder>() {

    fun updateData(newItems: List<Resort>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCustomerResortName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvCustomerResortLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_resort, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvLocation.text = item.location
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
} 