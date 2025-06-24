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
import com.example.quanlykhunghiduong.adapters.NotificationAdapter
import com.example.quanlykhunghiduong.models.Notification
import java.sql.DriverManager
import java.util.concurrent.Executors

class NotificationsFragment : Fragment() {
    private lateinit var rvNotifications: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private var userId: Int = -1

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
        private const val TAG = "NotificationsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
        Log.d(TAG, "NotificationsFragment được tạo với userId: $userId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvNotifications = view.findViewById(R.id.rvNotifications)
        notificationAdapter = NotificationAdapter(emptyList())
        rvNotifications.layoutManager = LinearLayoutManager(context)
        rvNotifications.adapter = notificationAdapter
        loadNotifications()
    }

    private fun loadNotifications() {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery(
                    "SELECT * FROM Notifications WHERE user_id = $userId ORDER BY sent_at DESC"
                )
                val notifications = mutableListOf<Notification>()
                while (resultSet.next()) {
                    notifications.add(
                        Notification(
                            notification_id = resultSet.getInt("notification_id"),
                            user_id = resultSet.getInt("user_id"),
                            title = resultSet.getString("title"),
                            message = resultSet.getString("message"),
                            sent_at = resultSet.getString("sent_at"),
                            is_read = resultSet.getBoolean("is_read")
                        )
                    )
                }
                resultSet.close()
                statement.close()
                connection.close()
                activity?.runOnUiThread {
                    notificationAdapter.updateData(notifications)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi load thông báo: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: Không thể tải thông báo", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 