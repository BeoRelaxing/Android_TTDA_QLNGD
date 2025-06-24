package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.RoomAdapter
import com.example.quanlykhunghiduong.models.Room
import java.sql.DriverManager
import java.util.concurrent.Executors

class AdminRoomActivity : AppCompatActivity() {
    private lateinit var etSearchRoom: EditText
    private lateinit var btnSearchRoom: Button
    private lateinit var rvRooms: RecyclerView
    private lateinit var etRoomNumber: EditText
    private lateinit var etRoomType: EditText
    private lateinit var etRoomPrice: EditText
    private lateinit var etRoomStatus: EditText
    private lateinit var etRoomCapacity: EditText
    private lateinit var etRoomResortId: EditText
    private lateinit var btnAddRoom: Button
    private lateinit var btnUpdateRoom: Button
    private lateinit var btnDeleteRoom: Button
    private lateinit var etSearchRoomType: EditText
    private lateinit var etSearchRoomStatus: EditText

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedRoom: Room? = null
    private val adapter by lazy {
        RoomAdapter(emptyList()) { room ->
            onSelect(room)
        }
    }

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_room)


        btnSearchRoom = findViewById(R.id.btnSearchRoom)
        rvRooms = findViewById(R.id.rvRooms)
        etRoomNumber = findViewById(R.id.etRoomNumber)
        etRoomType = findViewById(R.id.etRoomType)
        etRoomPrice = findViewById(R.id.etRoomPrice)
        etRoomStatus = findViewById(R.id.etRoomStatus)
        etRoomCapacity = findViewById(R.id.etRoomCapacity)
        etRoomResortId = findViewById(R.id.etRoomResortId)
        btnAddRoom = findViewById(R.id.btnAddRoom)
        btnUpdateRoom = findViewById(R.id.btnUpdateRoom)
        btnDeleteRoom = findViewById(R.id.btnDeleteRoom)
        etSearchRoomType = findViewById(R.id.etSearchRoomType)
        etSearchRoomStatus = findViewById(R.id.etSearchRoomStatus)

        rvRooms.layoutManager = LinearLayoutManager(this)
        rvRooms.adapter = adapter

        btnSearchRoom.setOnClickListener { searchRooms() }
        btnAddRoom.setOnClickListener { addRoom() }
        btnUpdateRoom.setOnClickListener { updateRoom() }
        btnDeleteRoom.setOnClickListener { deleteRoom() }

        loadRooms()
    }

    private fun onSelect(room: Room) {
        selectedRoom = room
        etRoomNumber.setText(room.room_number)
        etRoomType.setText(room.room_type)
        etRoomPrice.setText(room.price_per_night.toString())
        etRoomStatus.setText(room.status)
        etRoomCapacity.setText(room.capacity.toString())
        etRoomResortId.setText(room.resort_id.toString())
    }

    private fun clearFields() {
        etRoomNumber.setText("")
        etRoomType.setText("")
        etRoomPrice.setText("")
        etRoomStatus.setText("")
        etRoomCapacity.setText("")
        etRoomResortId.setText("")
        selectedRoom = null
    }

    private fun loadRooms() {
        executor.execute {
            val list = mutableListOf<Room>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Rooms")
                while (rs.next()) {
                    list.add(
                        Room(
                            room_id = rs.getInt("room_id"),
                            resort_id = rs.getInt("resort_id"),
                            room_number = rs.getString("room_number"),
                            room_type = rs.getString("room_type"),
                            price_per_night = rs.getDouble("price_per_night"),
                            status = rs.getString("status"),
                            capacity = rs.getInt("capacity")
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

    private fun searchRooms() {
        val typeKeyword = etSearchRoomType.text.toString().trim()
        val statusKeyword = etSearchRoomStatus.text.toString().trim()
        executor.execute {
            val list = mutableListOf<Room>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = StringBuilder("SELECT * FROM Rooms WHERE 1=1")
                if (typeKeyword.isNotEmpty()) sql.append(" AND room_type LIKE ?")
                if (statusKeyword.isNotEmpty()) sql.append(" AND status LIKE ?")
                val pstmt = conn.prepareStatement(sql.toString())
                var idx = 1
                if (typeKeyword.isNotEmpty()) {
                    pstmt.setString(idx++, "%$typeKeyword%")
                }
                if (statusKeyword.isNotEmpty()) {
                    pstmt.setString(idx, "%$statusKeyword%")
                }
                val rs = pstmt.executeQuery()
                while (rs.next()) {
                    list.add(
                        Room(
                            room_id = rs.getInt("room_id"),
                            resort_id = rs.getInt("resort_id"),
                            room_number = rs.getString("room_number"),
                            room_type = rs.getString("room_type"),
                            price_per_night = rs.getDouble("price_per_night"),
                            status = rs.getString("status"),
                            capacity = rs.getInt("capacity")
                        )
                    )
                }
                rs.close()
                pstmt.close()
                conn.close()
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi tìm kiếm: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            runOnUiThread {
                adapter.updateData(list)
            }
        }
    }

    private fun addRoom() {
        val roomNumber = etRoomNumber.text.toString().trim()
        val roomType = etRoomType.text.toString().trim()
        val price = etRoomPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val status = etRoomStatus.text.toString().trim()
        val capacity = etRoomCapacity.text.toString().trim().toIntOrNull() ?: 0
        val resortId = etRoomResortId.text.toString().trim().toIntOrNull() ?: 0
        if (roomNumber.isEmpty() || roomType.isEmpty() || resortId == 0) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "INSERT INTO Rooms (resort_id, room_number, room_type, price_per_night, status, capacity) VALUES (?, ?, ?, ?, ?, ?)"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, resortId)
                pstmt.setString(2, roomNumber)
                pstmt.setString(3, roomType)
                pstmt.setDouble(4, price)
                pstmt.setString(5, status)
                pstmt.setInt(6, capacity)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadRooms()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi thêm: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateRoom() {
        val room = selectedRoom ?: run {
            Toast.makeText(this, "Vui lòng chọn phòng để sửa", Toast.LENGTH_SHORT).show()
            return
        }
        val roomNumber = etRoomNumber.text.toString().trim()
        val roomType = etRoomType.text.toString().trim()
        val price = etRoomPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val status = etRoomStatus.text.toString().trim()
        val capacity = etRoomCapacity.text.toString().trim().toIntOrNull() ?: 0
        val resortId = etRoomResortId.text.toString().trim().toIntOrNull() ?: 0
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "UPDATE Rooms SET resort_id=?, room_number=?, room_type=?, price_per_night=?, status=?, capacity=? WHERE room_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, resortId)
                pstmt.setString(2, roomNumber)
                pstmt.setString(3, roomType)
                pstmt.setDouble(4, price)
                pstmt.setString(5, status)
                pstmt.setInt(6, capacity)
                pstmt.setInt(7, room.room_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadRooms()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun deleteRoom() {
        val room = selectedRoom ?: return
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "DELETE FROM Rooms WHERE room_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, room.room_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadRooms()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi xóa: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 