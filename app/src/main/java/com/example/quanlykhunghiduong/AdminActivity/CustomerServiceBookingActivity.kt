package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.ServiceBookingAdapter
import com.example.quanlykhunghiduong.models.ServiceBooking
import java.sql.DriverManager
import java.util.concurrent.Executors

class CustomerServiceBookingActivity : AppCompatActivity() {
    private lateinit var rvServiceBookings: RecyclerView
    private lateinit var etServiceQuantity: EditText
    private lateinit var btnUpdateServiceBooking: Button

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedServiceBooking: ServiceBooking? = null
    private val adapter by lazy {
        ServiceBookingAdapter(emptyList()) { serviceBooking ->
            onSelect(serviceBooking)
        }
    }

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_service_booking)

        rvServiceBookings = findViewById(R.id.rvServiceBookings)
        etServiceQuantity = findViewById(R.id.etServiceQuantity)
        btnUpdateServiceBooking = findViewById(R.id.btnUpdateServiceBooking)

        rvServiceBookings.layoutManager = LinearLayoutManager(this)
        rvServiceBookings.adapter = adapter

        btnUpdateServiceBooking.setOnClickListener { updateServiceBooking() }

        loadServiceBookings()
    }

    private fun onSelect(serviceBooking: ServiceBooking) {
        selectedServiceBooking = serviceBooking
        etServiceQuantity.setText(serviceBooking.quantity.toString())
    }

    private fun clearFields() {
        etServiceQuantity.setText("")
        selectedServiceBooking = null
    }

    private fun loadServiceBookings() {
        executor.execute {
            val list = mutableListOf<ServiceBooking>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Service_Bookings")
                while (rs.next()) {
                    list.add(
                        ServiceBooking(
                            service_booking_id = rs.getInt("service_booking_id"),
                            booking_id = rs.getInt("booking_id"),
                            service_id = rs.getInt("service_id"),
                            quantity = rs.getInt("quantity"),
                            total_price = rs.getDouble("total_price")
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

    private fun updateServiceBooking() {
        val serviceBooking = selectedServiceBooking ?: run {
            Toast.makeText(this, "Vui lòng chọn dịch vụ để cập nhật", Toast.LENGTH_SHORT).show()
            return
        }
        val newQuantity = etServiceQuantity.text.toString().trim().toIntOrNull() ?: 0
        if (newQuantity <= 0) {
            Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show()
            return
        }
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                // Lấy giá dịch vụ hiện tại
                val priceStmt = conn.prepareStatement("SELECT price FROM Services WHERE service_id = ?")
                priceStmt.setInt(1, serviceBooking.service_id)
                val priceRs = priceStmt.executeQuery()
                var price = 0.0
                if (priceRs.next()) {
                    price = priceRs.getDouble("price")
                }
                priceRs.close()
                priceStmt.close()
                val totalPrice = price * newQuantity
                // Cập nhật số lượng và tổng giá
                val sql = "UPDATE Service_Bookings SET quantity = ?, total_price = ? WHERE service_booking_id = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, newQuantity)
                pstmt.setDouble(2, totalPrice)
                pstmt.setInt(3, serviceBooking.service_booking_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadServiceBookings()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 