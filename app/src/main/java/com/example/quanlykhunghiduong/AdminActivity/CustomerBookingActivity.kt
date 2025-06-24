package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.BookingAdapter
import com.example.quanlykhunghiduong.models.Booking
import java.sql.DriverManager
import java.util.concurrent.Executors

class CustomerBookingActivity : AppCompatActivity() {
    private lateinit var rvBookings: RecyclerView
    private lateinit var btnConfirmBooking: Button
    private lateinit var btnCancelBooking: Button

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedBooking: Booking? = null
    private val adapter by lazy {
        BookingAdapter(emptyList()) { booking ->
            onSelect(booking)
        }
    }

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_booking)

        rvBookings = findViewById(R.id.rvBookings)
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking)
        btnCancelBooking = findViewById(R.id.btnCancelBooking)

        rvBookings.layoutManager = LinearLayoutManager(this)
        rvBookings.adapter = adapter

        btnConfirmBooking.setOnClickListener { confirmBooking() }
        btnCancelBooking.setOnClickListener { cancelBooking() }

        loadBookings()
    }

    private fun onSelect(booking: Booking) {
        selectedBooking = booking
    }

    private fun loadBookings() {
        executor.execute {
            val list = mutableListOf<Booking>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Bookings")
                while (rs.next()) {
                    list.add(
                        Booking(
                            booking_id = rs.getInt("booking_id"),
                            user_id = rs.getInt("user_id"),
                            room_id = rs.getInt("room_id"),
                            check_in_date = rs.getString("check_in_date"),
                            check_out_date = rs.getString("check_out_date"),
                            total_price = rs.getDouble("total_price"),
                            status = rs.getString("status"),
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
                adapter.updateData(list)
            }
        }
    }

    private fun confirmBooking() {
        val booking = selectedBooking ?: run {
            Toast.makeText(this, "Vui lòng chọn đặt phòng để xác nhận", Toast.LENGTH_SHORT).show()
            return
        }
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                // Cập nhật trạng thái đặt phòng
                val sql = "UPDATE Bookings SET status = 'checkedin' WHERE booking_id = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, booking.booking_id)
                pstmt.executeUpdate()
                pstmt.close()
                // Gửi thông báo
                val notifySql = "INSERT INTO Notifications (user_id, title, message, sent_at, is_read) VALUES (?, ?, ?, GETDATE(), 0)"
                val notifyPstmt = conn.prepareStatement(notifySql)
                notifyPstmt.setInt(1, booking.user_id)
                notifyPstmt.setString(2, "Đặt phòng xác nhận")
                notifyPstmt.setString(3, "Đặt phòng #${booking.booking_id} đã được xác nhận. Vui lòng đến nhận phòng đúng giờ.")
                notifyPstmt.executeUpdate()
                notifyPstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Xác nhận nhận phòng thành công!", Toast.LENGTH_SHORT).show()
                    loadBookings()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi xác nhận: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun cancelBooking() {
        val booking = selectedBooking ?: run {
            Toast.makeText(this, "Vui lòng chọn đặt phòng để hủy", Toast.LENGTH_SHORT).show()
            return
        }
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                // Cập nhật trạng thái đặt phòng
                val sql = "UPDATE Bookings SET status = 'Cancelled' WHERE booking_id = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, booking.booking_id)
                pstmt.executeUpdate()
                pstmt.close()
                // Gửi thông báo
                val notifySql = "INSERT INTO Notifications (user_id, title, message, sent_at, is_read) VALUES (?, ?, ?, GETDATE(), 0)"
                val notifyPstmt = conn.prepareStatement(notifySql)
                notifyPstmt.setInt(1, booking.user_id)
                notifyPstmt.setString(2, "Đặt phòng bị hủy")
                notifyPstmt.setString(3, "Đặt phòng #${booking.booking_id} đã bị hủy. Nếu có thắc mắc vui lòng liên hệ quản trị viên.")
                notifyPstmt.executeUpdate()
                notifyPstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Hủy đặt phòng thành công!", Toast.LENGTH_SHORT).show()
                    loadBookings()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi hủy đặt phòng: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 