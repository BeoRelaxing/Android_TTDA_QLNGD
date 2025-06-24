package com.example.quanlykhunghiduong.CustomerActivity

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CustomerBookingFragment : Fragment() {
    private lateinit var etSearchPrice: EditText
    private lateinit var etSearchStatus: EditText
    private lateinit var rvRooms: RecyclerView
    private lateinit var btnBookRoom: Button
    private lateinit var btnCancelBooking: Button
    private lateinit var roomAdapter: CustomerRoomAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private var allRooms: List<CustomerRoom> = emptyList()
    private var selectedRoom: CustomerRoom? = null
    private var userId: Int = -1
    private var resortId: Int = -1

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
        private const val TAG = "CustomerBookingFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resortId = arguments?.getInt("RESORT_ID", -1) ?: -1
        userId = arguments?.getInt(CustomerActivity.EXTRA_USER_ID, -1) ?: -1
        if (userId == -1) {
            userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
            Log.d(TAG, "Fallback lấy userId từ intent activity: $userId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSearchPrice = view.findViewById(R.id.etSearchPrice)
        etSearchStatus = view.findViewById(R.id.etSearchStatus)
        rvRooms = view.findViewById(R.id.rvCustomerRooms)
        btnBookRoom = view.findViewById(R.id.btnBookRoom)
        btnCancelBooking = view.findViewById(R.id.btnCancelBooking)
        roomAdapter = CustomerRoomAdapter(emptyList()) { room ->
            selectedRoom = room
        }
        rvRooms.layoutManager = LinearLayoutManager(context)
        rvRooms.adapter = roomAdapter
        loadRooms()

        etSearchPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRooms()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        etSearchStatus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRooms()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnBookRoom.setOnClickListener {
            if (selectedRoom == null) {
                Toast.makeText(context, "Vui lòng chọn phòng để đặt!", Toast.LENGTH_SHORT).show()
            } else {
                bookRoom(selectedRoom!!)
            }
        }
        btnCancelBooking.setOnClickListener {
            if (selectedRoom == null) {
                Toast.makeText(context, "Vui lòng chọn phòng để hủy!", Toast.LENGTH_SHORT).show()
            } else {
                cancelBooking(selectedRoom!!)
            }
        }
    }

    private fun loadRooms() {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM Rooms WHERE resort_id = $resortId")
                val rooms = mutableListOf<CustomerRoom>()
                while (resultSet.next()) {
                    rooms.add(
                        CustomerRoom(
                            room_id = resultSet.getInt("room_id"),
                            resort_id = resultSet.getInt("resort_id"),
                            room_number = resultSet.getString("room_number"),
                            room_type = resultSet.getString("room_type"),
                            price_per_night = resultSet.getDouble("price_per_night"),
                            status = resultSet.getString("status"),
                            capacity = resultSet.getInt("capacity")
                        )
                    )
                }
                resultSet.close()
                statement.close()
                connection.close()
                allRooms = rooms
                activity?.runOnUiThread {
                    roomAdapter.updateData(rooms)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi load phòng: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: Không thể tải danh sách phòng", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun filterRooms() {
        val priceQuery = etSearchPrice.text.toString().trim()
        val statusQuery = etSearchStatus.text.toString().trim()
        val filtered = allRooms.filter {
            (priceQuery.isEmpty() || it.price_per_night.toString().contains(priceQuery)) &&
            (statusQuery.isEmpty() || it.status.contains(statusQuery, ignoreCase = true))
        }
        roomAdapter.updateData(filtered)
    }

    private fun bookRoom(room: CustomerRoom) {
        if (room.status != "Available") {
            Toast.makeText(context, "Phòng này không khả dụng để đặt!", Toast.LENGTH_SHORT).show()
            return
        }
        val today = Calendar.getInstance()
        val checkInDate = Calendar.getInstance()
        val checkOutDate = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            checkInDate.set(year, month, dayOfMonth)
            DatePickerDialog(requireContext(), { _, y2, m2, d2 ->
                checkOutDate.set(y2, m2, d2)
                if (!checkOutDate.after(checkInDate)) {
                    Toast.makeText(context, "Ngày check-out phải sau check-in!", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }
                checkRoomBookingAndInsert(room, checkInDate, checkOutDate)
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show()
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun checkRoomBookingAndInsert(room: CustomerRoom, checkIn: Calendar, checkOut: Calendar) {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = """
                    SELECT status FROM Bookings 
                    WHERE room_id = ? AND 
                    (check_in_date < ? AND check_out_date > ?)
                """.trimIndent()
                val stmt = connection.prepareStatement(sql)
                stmt.setInt(1, room.room_id)
                stmt.setString(2, formatDate(checkOut.time))
                stmt.setString(3, formatDate(checkIn.time))
                val rs = stmt.executeQuery()
                var hasCheckedInConflict = false
                while (rs.next()) {
                    val status = rs.getString("status")
                    if (status.equals("CheckedIn", ignoreCase = true)) {
                        hasCheckedInConflict = true
                        break
                    }
                }
                if (hasCheckedInConflict) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Phòng đã được đặt trong khoảng thời gian này!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (userId == -1) {
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Lỗi: Không xác định được tài khoản. Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show()
                        }
                        return@execute
                    }
                    val insert = connection.prepareStatement(

                        "INSERT INTO Bookings (user_id, room_id, check_in_date, check_out_date, total_price, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)"
                    )
                    val userId = getCurrentUserId()
                    val days = ((checkOut.timeInMillis - checkIn.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                    val totalPrice = days * room.price_per_night
                    insert.setInt(1, userId)
                    insert.setInt(2, room.room_id)
                    insert.setString(3, formatDate(checkIn.time))
                    insert.setString(4, formatDate(checkOut.time))
                    insert.setDouble(5, totalPrice)
                    insert.setString(6, "Pending")
                    insert.setString(7, formatDate(Date()))
                    insert.executeUpdate()
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Đặt phòng thành công!", Toast.LENGTH_SHORT).show()
                        loadRooms()
                    }
                    insert.close()
                }
                rs.close()
                stmt.close()
                connection.close()
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi đặt phòng: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(date)
    }

    private fun getCurrentUserId(): Int {
        if (userId == -1) {
            userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
            Log.d(TAG, "Fallback getCurrentUserId từ intent activity: $userId")
        }
        return userId
    }

    private fun cancelBooking(room: CustomerRoom) {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val userId = getCurrentUserId()
                val stmt = connection.prepareStatement(
                    "SELECT booking_id FROM Bookings WHERE user_id = ? AND room_id = ? AND status = 'checkedin'"
                )
                stmt.setInt(1, userId)
                stmt.setInt(2, room.room_id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val bookingId = rs.getInt("booking_id")
                    val del = connection.prepareStatement("DELETE FROM Bookings WHERE booking_id = ?")
                    del.setInt(1, bookingId)
                    del.executeUpdate()
                    del.close()
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Hủy đặt phòng thành công!", Toast.LENGTH_SHORT).show()
                        loadRooms()
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Bạn không có đặt phòng nào để hủy!", Toast.LENGTH_SHORT).show()
                    }
                }
                rs.close()
                stmt.close()
                connection.close()
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi hủy đặt phòng: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 