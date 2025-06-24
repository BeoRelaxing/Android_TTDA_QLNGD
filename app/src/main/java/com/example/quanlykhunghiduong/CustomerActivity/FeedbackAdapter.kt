package com.example.quanlykhunghiduong.CustomerActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.Feedback

class FeedbackAdapter : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {
    private var feedbacks: List<Feedback> = emptyList()

    fun updateFeedbacks(newFeedbacks: List<Feedback>) {
        feedbacks = newFeedbacks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.bind(feedbacks[position])
    }

    override fun getItemCount() = feedbacks.size

    class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.feedback_content)
        private val dateTextView: TextView = itemView.findViewById(R.id.feedback_date)

        fun bind(feedback: Feedback) {
            contentTextView.text = feedback.content
            dateTextView.text = feedback.created_at
        }
    }
} 