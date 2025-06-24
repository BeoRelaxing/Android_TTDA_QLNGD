package com.example.quanlykhunghiduong.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.User

class UserAdapter(
    private var items: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var selectedUserId: Int? = null

    fun updateData(newItems: List<User>) {
        items = newItems
        selectedUserId = null
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: LinearLayout = itemView.findViewById(R.id.itemUserRoot)
        val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        val tvUserPhone: TextView = itemView.findViewById(R.id.tvUserPhone)
        val tvUserRole: TextView = itemView.findViewById(R.id.tvUserRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvUserId.text = item.user_id.toString()
        holder.tvUserName.text = item.name
        holder.tvUserEmail.text = item.email
        holder.tvUserPhone.text = item.phone
        holder.tvUserRole.text = item.role

        if (item.user_id == selectedUserId) {
            holder.root.setBackgroundColor(Color.parseColor("#FFDDDD"))
        } else {
            holder.root.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            selectedUserId = item.user_id
            notifyDataSetChanged()
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
} 