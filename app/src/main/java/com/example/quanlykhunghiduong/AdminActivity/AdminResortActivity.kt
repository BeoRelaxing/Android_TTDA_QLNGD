package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.ResortAdapter
import com.example.quanlykhunghiduong.models.Resort
import java.sql.DriverManager
import java.util.concurrent.Executors

class AdminResortActivity : AppCompatActivity() {
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var rvResorts: RecyclerView
    private lateinit var etName: EditText
    private lateinit var etLocation: EditText
    private lateinit var etType: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPriceRange: EditText
    private lateinit var etAmenities: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedResort: Resort? = null
    private val adapter by lazy {
        ResortAdapter(emptyList()) { resort ->
            onSelect(resort)
        }
    }

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_resort)

        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        rvResorts = findViewById(R.id.rvResorts)
        etName = findViewById(R.id.etName)
        etLocation = findViewById(R.id.etLocation)
        etType = findViewById(R.id.etType)
        etDescription = findViewById(R.id.etDescription)
        etPriceRange = findViewById(R.id.etPriceRange)
        etAmenities = findViewById(R.id.etAmenities)
        btnAdd = findViewById(R.id.btnAdd)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)

        rvResorts.layoutManager = LinearLayoutManager(this)
        rvResorts.adapter = adapter

        btnSearch.setOnClickListener { searchResorts() }
        btnAdd.setOnClickListener { addResort() }
        btnUpdate.setOnClickListener { updateResort() }
        btnDelete.setOnClickListener { deleteResort() }

        loadResorts()
    }

    private fun onSelect(resort: Resort) {
        selectedResort = resort
        etName.setText(resort.name)
        etLocation.setText(resort.location)
        etType.setText(resort.type)
        etDescription.setText(resort.description)
        etPriceRange.setText(resort.price_range)
        etAmenities.setText(resort.amenities)
    }

    private fun clearFields() {
        etName.setText("")
        etLocation.setText("")
        etType.setText("")
        etDescription.setText("")
        etPriceRange.setText("")
        etAmenities.setText("")
        selectedResort = null
    }

    private fun loadResorts() {
        executor.execute {
            val list = mutableListOf<Resort>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Resorts")
                while (rs.next()) {
                    list.add(
                        Resort(
                            resort_id = rs.getInt("resort_id"),
                            name = rs.getString("name"),
                            location = rs.getString("location"),
                            type = rs.getString("type"),
                            description = rs.getString("description"),
                            price_range = rs.getString("price_range"),
                            amenities = rs.getString("amenities"),
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

    private fun searchResorts() {
        val keyword = etSearch.text.toString().trim()
        executor.execute {
            val list = mutableListOf<Resort>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "SELECT * FROM Resorts WHERE name LIKE ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setString(1, "%$keyword%")
                val rs = pstmt.executeQuery()
                while (rs.next()) {
                    list.add(
                        Resort(
                            resort_id = rs.getInt("resort_id"),
                            name = rs.getString("name"),
                            location = rs.getString("location"),
                            type = rs.getString("type"),
                            description = rs.getString("description"),
                            price_range = rs.getString("price_range"),
                            amenities = rs.getString("amenities"),
                            created_at = rs.getString("created_at")
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

    private fun addResort() {
        val name = etName.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val type = etType.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val priceRange = etPriceRange.text.toString().trim()
        val amenities = etAmenities.text.toString().trim()
        if (name.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "INSERT INTO Resorts (name, location, type, description, price_range, amenities, created_at) VALUES (?, ?, ?, ?, ?, ?, GETDATE())"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setString(1, name)
                pstmt.setString(2, location)
                pstmt.setString(3, type)
                pstmt.setString(4, description)
                pstmt.setString(5, priceRange)
                pstmt.setString(6, amenities)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadResorts()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi thêm: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateResort() {
        val resort = selectedResort ?: run {
            Toast.makeText(this, "Vui lòng chọn khu nghỉ dưỡng để sửa", Toast.LENGTH_SHORT).show()
            return
        }
        val name = etName.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val type = etType.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val priceRange = etPriceRange.text.toString().trim()
        val amenities = etAmenities.text.toString().trim()
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "UPDATE Resorts SET name=?, location=?, type=?, description=?, price_range=?, amenities=? WHERE resort_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setString(1, name)
                pstmt.setString(2, location)
                pstmt.setString(3, type)
                pstmt.setString(4, description)
                pstmt.setString(5, priceRange)
                pstmt.setString(6, amenities)
                pstmt.setInt(7, resort.resort_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadResorts()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun deleteResort() {
        val resort = selectedResort ?: return
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "DELETE FROM Resorts WHERE resort_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, resort.resort_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadResorts()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi xóa: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 