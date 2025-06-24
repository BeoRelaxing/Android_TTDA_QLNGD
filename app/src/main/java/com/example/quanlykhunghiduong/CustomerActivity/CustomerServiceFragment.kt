package com.example.quanlykhunghiduong.CustomerActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.ServiceAdapter
import com.example.quanlykhunghiduong.adapters.ServiceBookingAdapter
import com.example.quanlykhunghiduong.models.Service
import com.example.quanlykhunghiduong.models.ServiceBooking
import java.sql.DriverManager
import java.util.concurrent.Executors

class CustomerServiceFragment : Fragment() {
    private lateinit var rvServices: RecyclerView
    private lateinit var rvOrderedServices: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var orderedServiceAdapter: ServiceBookingAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private var resortId: Int = -1
    private var userId: Int = -1
    private var bookingId: Int = -1

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
        private const val TAG = "CustomerServiceFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resortId = arguments?.getInt("RESORT_ID", -1) ?: -1
        userId = arguments?.getInt(CustomerActivity.EXTRA_USER_ID, -1) ?: -1
        rvServices = view.findViewById(R.id.rvServices)
        rvOrderedServices = view.findViewById(R.id.rvOrderedServices)
        serviceAdapter = ServiceAdapter(emptyList()) { service ->
            // Khi click vào dịch vụ, đặt dịch vụ với số lượng mặc định là 1
            orderService(service, 1)
        }
        orderedServiceAdapter = ServiceBookingAdapter(emptyList()) { serviceBooking ->
            cancelServiceBooking(serviceBooking.service_booking_id)
        }
        rvServices.layoutManager = LinearLayoutManager(context)
        rvServices.adapter = serviceAdapter
        rvOrderedServices.layoutManager = LinearLayoutManager(context)
        rvOrderedServices.adapter = orderedServiceAdapter
        getCurrentBookingIdAndLoad()
    }

    private fun getCurrentBookingIdAndLoad() {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = connection.prepareStatement(
                    "SELECT booking_id FROM Bookings WHERE user_id = ? AND status IN ('Pending', 'CheckedIn') AND room_id IN (SELECT room_id FROM Rooms WHERE resort_id = ?)"
                )
                stmt.setInt(1, userId)
                stmt.setInt(2, resortId)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    bookingId = rs.getInt("booking_id")
                }
                rs.close()
                stmt.close()
                connection.close()
                activity?.runOnUiThread {
                    loadServices()
                    loadOrderedServices()
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi lấy booking: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadServices() {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = connection.prepareStatement(
                    "SELECT * FROM Services WHERE resort_id = ?"
                )
                stmt.setInt(1, resortId)
                val rs = stmt.executeQuery()
                val services = mutableListOf<Service>()
                while (rs.next()) {
                    services.add(
                        Service(
                            service_id = rs.getInt("service_id"),
                            resort_id = rs.getInt("resort_id"),
                            name = rs.getString("name"),
                            description = rs.getString("description"),
                            price = rs.getDouble("price")
                        )
                    )
                }
                rs.close()
                stmt.close()
                connection.close()
                activity?.runOnUiThread {
                    serviceAdapter.updateData(services)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi tải dịch vụ: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadOrderedServices() {
        if (bookingId == -1) return
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = connection.prepareStatement(
                    "SELECT * FROM Service_Bookings WHERE booking_id = ?"
                )
                stmt.setInt(1, bookingId)
                val rs = stmt.executeQuery()
                val ordered = mutableListOf<ServiceBooking>()
                while (rs.next()) {
                    ordered.add(
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
                connection.close()
                activity?.runOnUiThread {
                    orderedServiceAdapter.updateData(ordered)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi tải dịch vụ đã đặt: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun orderService(service: Service, quantity: Int) {
        if (bookingId == -1) {
            Toast.makeText(context, "Bạn chưa có đặt phòng!", Toast.LENGTH_SHORT).show()
            return
        }
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val totalPrice = service.price * quantity
                val stmt = connection.prepareStatement(
                    "INSERT INTO Service_Bookings (booking_id, service_id, quantity, total_price) VALUES (?, ?, ?, ?)"
                )
                stmt.setInt(1, bookingId)
                stmt.setInt(2, service.service_id)
                stmt.setInt(3, quantity)
                stmt.setDouble(4, totalPrice)
                stmt.executeUpdate()
                stmt.close()
                connection.close()
                activity?.runOnUiThread {
                    Toast.makeText(context, "Đặt dịch vụ thành công!", Toast.LENGTH_SHORT).show()
                    loadOrderedServices()
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi đặt dịch vụ: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun cancelServiceBooking(serviceBookingId: Int) {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = connection.prepareStatement(
                    "DELETE FROM Service_Bookings WHERE service_booking_id = ?"
                )
                stmt.setInt(1, serviceBookingId)
                stmt.executeUpdate()
                stmt.close()
                connection.close()
                activity?.runOnUiThread {
                    Toast.makeText(context, "Hủy dịch vụ thành công!", Toast.LENGTH_SHORT).show()
                    loadOrderedServices()
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi hủy dịch vụ: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 