package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.NotificationAdapter
import com.example.quanlykhunghiduong.models.Notification
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class NotificationActivity : AppCompatActivity() {
    private lateinit var etNotificationTitle: EditText
    private lateinit var etNotificationContent: EditText
    private lateinit var btnSendNotification: Button
    private lateinit var btnDeleteNotification: Button
    private lateinit var rvNotifications: RecyclerView

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedNotification: Notification? = null
    val adapter = NotificationAdapter(listOf<Notification>(/* các phần tử */))

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        etNotificationTitle = findViewById(R.id.etNotificationTitle)
        etNotificationContent = findViewById(R.id.etNotificationContent)
        btnSendNotification = findViewById(R.id.btnSendNotification)
        btnDeleteNotification = findViewById(R.id.btnDeleteNotification)
        rvNotifications = findViewById(R.id.rvNotifications)

        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = adapter

        btnSendNotification.setOnClickListener { sendNotification() }
        btnDeleteNotification.setOnClickListener { deleteNotification() }

        loadNotifications()
    }

    private fun onSelect(notification: Notification) {
        selectedNotification = notification
    }

    private fun clearFields() {
        etNotificationTitle.setText("")
        etNotificationContent.setText("")
        selectedNotification = null
    }

    private fun loadNotifications() {
        executor.execute {
            val list = mutableListOf<Notification>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Notifications ORDER BY sent_at DESC")
                while (rs.next()) {
                    list.add(
                        Notification(
                            notification_id = rs.getInt("notification_id"),
                            user_id = rs.getInt("user_id"),
                            title = rs.getString("title"),
                            message = rs.getString("message"),
                            sent_at = rs.getString("sent_at"),
                            is_read = rs.getBoolean("is_read")
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
                adapter.updateData(list)
            }
        }
    }

    private fun sendNotification() {
        val title = etNotificationTitle.text.toString().trim()
        val content = etNotificationContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentTime = dateFormat.format(Date())
                
                val sql = "INSERT INTO Notifications (user_id, title, message, sent_at, is_read) VALUES (?, ?, ?, ?, ?)"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, 1) // Mặc định user_id = 1 cho admin
                pstmt.setString(2, title)
                pstmt.setString(3, content)
                pstmt.setString(4, currentTime)
                pstmt.setBoolean(5, false) // Mặc định is_read = false
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Gửi thông báo thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadNotifications()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi gửi thông báo: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun deleteNotification() {
        val notification = selectedNotification ?: run {
            Toast.makeText(this, "Vui lòng chọn thông báo để xóa", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "DELETE FROM Notifications WHERE notification_id = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, notification.notification_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Xóa thông báo thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadNotifications()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi xóa thông báo: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 