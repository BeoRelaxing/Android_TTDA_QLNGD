package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.ServiceAdapter
import com.example.quanlykhunghiduong.models.Service
import java.sql.DriverManager
import java.util.concurrent.Executors

class AdminServiceActivity : AppCompatActivity() {
    private lateinit var rvServices: RecyclerView
    private lateinit var etServiceName: EditText
    private lateinit var etServiceDescription: EditText
    private lateinit var etServicePrice: EditText
    private lateinit var etServiceResortId: EditText
    private lateinit var btnAddService: Button
    private lateinit var btnUpdateService: Button
    private lateinit var btnDeleteService: Button

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedService: Service? = null
    private val adapter by lazy {
        ServiceAdapter(emptyList()) { service ->
            onSelect(service)
        }
    }

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_service)

        rvServices = findViewById(R.id.rvServices)
        etServiceName = findViewById(R.id.etServiceName)
        etServiceDescription = findViewById(R.id.etServiceDescription)
        etServicePrice = findViewById(R.id.etServicePrice)
        etServiceResortId = findViewById(R.id.etServiceResortId)
        btnAddService = findViewById(R.id.btnAddService)
        btnUpdateService = findViewById(R.id.btnUpdateService)
        btnDeleteService = findViewById(R.id.btnDeleteService)

        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.adapter = adapter

        btnAddService.setOnClickListener { addService() }
        btnUpdateService.setOnClickListener { updateService() }
        btnDeleteService.setOnClickListener { deleteService() }

        loadServices()
    }

    private fun onSelect(service: Service) {
        selectedService = service
        etServiceName.setText(service.name)
        etServiceDescription.setText(service.description)
        etServicePrice.setText(service.price.toString())
        etServiceResortId.setText(service.resort_id.toString())
    }

    private fun clearFields() {
        etServiceName.setText("")
        etServiceDescription.setText("")
        etServicePrice.setText("")
        etServiceResortId.setText("")
        selectedService = null
    }

    private fun loadServices() {
        executor.execute {
            val list = mutableListOf<Service>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Services")
                while (rs.next()) {
                    list.add(
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

    private fun addService() {
        val name = etServiceName.text.toString().trim()
        val description = etServiceDescription.text.toString().trim()
        val price = etServicePrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val resortId = etServiceResortId.text.toString().trim().toIntOrNull() ?: 0
        if (name.isEmpty() || resortId == 0) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "INSERT INTO Services (resort_id, name, description, price) VALUES (?, ?, ?, ?)"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, resortId)
                pstmt.setString(2, name)
                pstmt.setString(3, description)
                pstmt.setDouble(4, price)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadServices()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi thêm: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateService() {
        val service = selectedService ?: run {
            Toast.makeText(this, "Vui lòng chọn dịch vụ để sửa", Toast.LENGTH_SHORT).show()
            return
        }
        val name = etServiceName.text.toString().trim()
        val description = etServiceDescription.text.toString().trim()
        val price = etServicePrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val resortId = etServiceResortId.text.toString().trim().toIntOrNull() ?: 0
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "UPDATE Services SET resort_id=?, name=?, description=?, price=? WHERE service_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, resortId)
                pstmt.setString(2, name)
                pstmt.setString(3, description)
                pstmt.setDouble(4, price)
                pstmt.setInt(5, service.service_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadServices()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun deleteService() {
        val service = selectedService ?: return
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "DELETE FROM Services WHERE service_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, service.service_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadServices()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi xóa: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 