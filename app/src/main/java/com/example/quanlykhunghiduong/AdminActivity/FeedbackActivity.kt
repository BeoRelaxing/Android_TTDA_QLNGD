package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.FeedbackADMAdapter
import com.example.quanlykhunghiduong.models.Feedback
import java.sql.DriverManager
import java.util.concurrent.Executors

class FeedbackActivity : AppCompatActivity() {
    private lateinit var btnDeleteFeedback: Button
    private lateinit var rvFeedbacks: RecyclerView

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedFeedback: Feedback? = null
    private val adapter by lazy {
        FeedbackADMAdapter(emptyList()) { feedback ->
            onSelect(feedback)
        }
    }

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        btnDeleteFeedback = findViewById(R.id.btnDeleteFeedback)
        rvFeedbacks = findViewById(R.id.rvFeedbacks)

        rvFeedbacks.layoutManager = LinearLayoutManager(this)
        rvFeedbacks.adapter = adapter

        btnDeleteFeedback.setOnClickListener { deleteFeedback() }

        loadFeedbacks()
    }

    private fun onSelect(feedback: Feedback) {
        selectedFeedback = feedback
    }

    private fun loadFeedbacks() {
        executor.execute {
            val list = mutableListOf<Feedback>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Feedback ORDER BY created_at DESC")
                while (rs.next()) {
                    list.add(
                        Feedback(
                            feedback_id = rs.getInt("feedback_id"),
                            user_id = rs.getInt("user_id"),
                            content = rs.getString("content"),
                            created_at = rs.getString("created_at")
                        )
                    )
                }
                rs.close()
                stmt.close()
                conn.close()
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            runOnUiThread {
                adapter.updateFeedbacks(list)
            }
        }
    }

    private fun deleteFeedback() {
        val feedback = selectedFeedback ?: run {
            Toast.makeText(this, "Vui lòng chọn đánh giá để xóa", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "DELETE FROM Feedback WHERE feedback_id = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, feedback.feedback_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Xóa đánh giá thành công!", Toast.LENGTH_SHORT).show()
                    selectedFeedback = null
                    loadFeedbacks()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi xóa đánh giá: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 