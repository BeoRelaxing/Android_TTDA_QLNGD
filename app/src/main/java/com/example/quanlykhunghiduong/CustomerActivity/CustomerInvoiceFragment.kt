package com.example.quanlykhunghiduong.CustomerActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quanlykhunghiduong.R
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.CustomerActivity.CustomerActivity
import java.sql.DriverManager
import java.util.concurrent.Executors

class CustomerInvoiceFragment : Fragment() {
    private lateinit var tvRoomInfo: TextView
    private lateinit var tvBookingDate: TextView
    private lateinit var tvRoomPrice: TextView
    private lateinit var tvBookingStatus: TextView
    private lateinit var tvTotalRoom: TextView
    private lateinit var tvTotalService: TextView
    private lateinit var tvTotalAll: TextView
    private lateinit var rvInvoiceServices: RecyclerView
    private lateinit var serviceAdapter: InvoiceServiceAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private var resortId: Int = -1
    private var userId: Int = -1
    private var bookingId: Int = -1

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_invoice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvRoomInfo = view.findViewById(R.id.tvRoomInfo)
        tvBookingDate = view.findViewById(R.id.tvBookingDate)
        tvRoomPrice = view.findViewById(R.id.tvRoomPrice)
        tvBookingStatus = view.findViewById(R.id.tvBookingStatus)
        tvTotalRoom = view.findViewById(R.id.tvTotalRoom)
        tvTotalService = view.findViewById(R.id.tvTotalService)
        tvTotalAll = view.findViewById(R.id.tvTotalAll)
        rvInvoiceServices = view.findViewById(R.id.rvInvoiceServices)
        serviceAdapter = InvoiceServiceAdapter(emptyList())
        rvInvoiceServices.layoutManager = LinearLayoutManager(context)
        rvInvoiceServices.adapter = serviceAdapter

        resortId = arguments?.getInt("RESORT_ID", -1) ?: -1
        userId = arguments?.getInt(CustomerActivity.EXTRA_USER_ID, -1) ?: -1

        loadInvoice()
    }

    private fun loadInvoice() {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                // Lấy booking hiện tại
                val stmt = connection.prepareStatement(
                    """SELECT b.*, r.room_number, r.room_type, r.price_per_night FROM Bookings b JOIN Rooms r ON b.room_id = r.room_id WHERE b.user_id = ? AND b.status IN ('Pending', 'CheckedIn') AND r.resort_id = ?"""
                )
                stmt.setInt(1, userId)
                stmt.setInt(2, resortId)
                val rs = stmt.executeQuery()
                var roomInfo = ""
                var bookingDate = ""
                var roomPrice = 0.0
                var bookingStatus = ""
                var bookingTotal = 0.0
                if (rs.next()) {
                    bookingId = rs.getInt("booking_id")
                    roomInfo = "Phòng: ${rs.getString("room_number")}" +
                        " (${rs.getString("room_type")})"
                    bookingDate = "Từ ${rs.getString("check_in_date")} đến ${rs.getString("check_out_date")}" 
                    roomPrice = rs.getDouble("total_price")
                    bookingStatus = "Trạng thái: ${rs.getString("status")}" 
                    bookingTotal = roomPrice
                }
                rs.close()
                stmt.close()

                // Lấy dịch vụ đã đặt
                var totalService = 0.0
                val serviceList = mutableListOf<InvoiceService>()
                if (bookingId != -1) {
                    val stmt2 = connection.prepareStatement(
                        """SELECT sb.quantity, sb.total_price, s.name FROM Service_Bookings sb JOIN Services s ON sb.service_id = s.service_id WHERE sb.booking_id = ?"""
                    )
                    stmt2.setInt(1, bookingId)
                    val rs2 = stmt2.executeQuery()
                    while (rs2.next()) {
                        val name = rs2.getString("name")
                        val quantity = rs2.getInt("quantity")
                        val total = rs2.getDouble("total_price")
                        totalService += total
                        serviceList.add(InvoiceService(name, quantity, total))
                    }
                    rs2.close()
                    stmt2.close()
                }
                connection.close()
                val totalAll = bookingTotal + totalService

                activity?.runOnUiThread {
                    tvRoomInfo.text = roomInfo
                    tvBookingDate.text = bookingDate
                    tvRoomPrice.text = "Tiền phòng: $roomPrice"
                    tvBookingStatus.text = bookingStatus
                    tvTotalRoom.text = "Tiền phòng: $roomPrice"
                    tvTotalService.text = "Tiền dịch vụ: $totalService"
                    tvTotalAll.text = "Tổng cộng: $totalAll"
                    serviceAdapter.updateData(serviceList)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi tải hóa đơn: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


data class InvoiceService(
    val name: String,
    val quantity: Int,
    val total_price: Double
)

class InvoiceServiceAdapter(
    private var items: List<InvoiceService>
) : RecyclerView.Adapter<InvoiceServiceAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvServiceName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvServiceQuantity)
        val tvTotal: TextView = itemView.findViewById(R.id.tvServiceTotal)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice_service, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvQuantity.text = "x${item.quantity}"
        holder.tvTotal.text = "${item.total_price}đ"
    }
    override fun getItemCount(): Int = items.size
    fun updateData(newItems: List<InvoiceService>) {
        items = newItems
        notifyDataSetChanged()
    }
}