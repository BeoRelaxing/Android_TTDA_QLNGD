package com.example.quanlykhunghiduong.CustomerActivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.Feedback
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class FeedbackFragment : Fragment() {
    private lateinit var feedbackContentEditText: TextInputEditText
    private lateinit var submitFeedbackButton: MaterialButton
    private lateinit var feedbackRecyclerView: RecyclerView
    private lateinit var feedbackAdapter: FeedbackAdapter

    private val executor = Executors.newSingleThreadExecutor()
    private var userId: Int = -1

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
        private const val TAG = "FeedbackFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lấy userId từ intent
        userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
        Log.d(TAG, "FeedbackFragment được tạo với userId: $userId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo các view
        feedbackContentEditText = view.findViewById(R.id.feedback_content_edit_text)
        submitFeedbackButton = view.findViewById(R.id.submit_feedback_button)
        feedbackRecyclerView = view.findViewById(R.id.feedback_recycler_view)

        // Thiết lập RecyclerView
        feedbackAdapter = FeedbackAdapter()
        feedbackRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = feedbackAdapter
        }

        // Kiểm tra userId
        if (userId == -1) {
            // Thử lấy userId từ intent một lần nữa
            userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
            Log.d(TAG, "Thử lấy userId lần nữa: $userId")
        }

        if (userId == -1) {
            Log.e(TAG, "Không nhận được userId")
            Toast.makeText(context, "Lỗi: Không thể tải đánh giá", Toast.LENGTH_LONG).show()
            return
        }

        // Load danh sách đánh giá
        loadFeedbacks()

        // Xử lý sự kiện gửi đánh giá
        submitFeedbackButton.setOnClickListener {
            Log.d(TAG, "Nút gửi đánh giá được ấn")
            submitFeedback()
        }
    }

    override fun onResume() {
        super.onResume()
        // Kiểm tra userId trước khi load đánh giá
        if (userId != -1) {
            loadFeedbacks()
        } else {
            // Thử lấy userId từ intent một lần nữa
            userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
            if (userId != -1) {
                loadFeedbacks()
            } else {
                Log.e(TAG, "Không thể lấy userId trong onResume")
            }
        }
    }

    private fun loadFeedbacks() {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery(
                    "SELECT * FROM Feedback WHERE user_id = $userId ORDER BY created_at DESC"
                )

                val feedbacks = mutableListOf<Feedback>()
                while (resultSet.next()) {
                    feedbacks.add(
                        Feedback(
                            feedback_id = resultSet.getInt("feedback_id"),
                            user_id = resultSet.getInt("user_id"),
                            content = resultSet.getString("content"),
                            created_at = resultSet.getString("created_at")
                        )
                    )
                }

                activity?.runOnUiThread {
                    feedbackAdapter.updateFeedbacks(feedbacks)
                }

                resultSet.close()
                statement.close()
                connection.close()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi load đánh giá: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: Không thể tải đánh giá", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun submitFeedback() {
        val content = feedbackContentEditText.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date())

                val statement = connection.prepareStatement(
                    "INSERT INTO Feedback (user_id, content, created_at) VALUES (?, ?, ?)"
                )
                statement.setInt(1, userId)
                statement.setString(2, content)
                statement.setString(3, currentDate)
                statement.executeUpdate()

                statement.close()
                connection.close()

                activity?.runOnUiThread {
                    feedbackContentEditText.text?.clear()
                    Toast.makeText(context, "Gửi đánh giá thành công", Toast.LENGTH_SHORT).show()
                    loadFeedbacks() // Load lại danh sách đánh giá
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi gửi đánh giá: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: Không thể gửi đánh giá", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}