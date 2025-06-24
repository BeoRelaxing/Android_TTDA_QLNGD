package com.example.quanlykhunghiduong.AdminActivity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.adapters.UserAdapter
import com.example.quanlykhunghiduong.models.User
import java.sql.DriverManager
import java.util.concurrent.Executors

class AdminUserActivity : AppCompatActivity() {
    private lateinit var rvUsers: RecyclerView
    private lateinit var etUserName: EditText
    private lateinit var etUserEmail: EditText
    private lateinit var etUserPassword: EditText
    private lateinit var etUserPhone: EditText
    private lateinit var etUserRole: EditText
    private lateinit var btnUpdateUser: Button
    private lateinit var btnDeleteUser: Button

    private val executor = Executors.newSingleThreadExecutor()
    private var selectedUser: User? = null
    private val adapter by lazy {
        UserAdapter(emptyList()) { user ->
            onSelect(user)
        }
    }

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user)

        rvUsers = findViewById(R.id.rvUsers)
        etUserName = findViewById(R.id.etUserName)
        etUserEmail = findViewById(R.id.etUserEmail)
        etUserPassword = findViewById(R.id.etUserPassword)
        etUserPhone = findViewById(R.id.etUserPhone)
        etUserRole = findViewById(R.id.etUserRole)
        btnUpdateUser = findViewById(R.id.btnUpdateUser)
        btnDeleteUser = findViewById(R.id.btnDeleteUser)

        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter

        btnUpdateUser.setOnClickListener { updateUser() }
        btnDeleteUser.setOnClickListener { deleteUser() }

        loadUsers()
    }

    private fun onSelect(user: User) {
        selectedUser = user
        etUserName.setText(user.name)
        etUserEmail.setText(user.email)
        etUserPassword.setText(user.password_hash)
        etUserPhone.setText(user.phone)
        etUserRole.setText(user.role)
    }

    private fun clearFields() {
        etUserName.setText("")
        etUserEmail.setText("")
        etUserPassword.setText("")
        etUserPhone.setText("")
        etUserRole.setText("")
        selectedUser = null
    }

    private fun loadUsers() {
        executor.execute {
            val list = mutableListOf<User>()
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM Users")
                while (rs.next()) {
                    list.add(
                        User(
                            user_id = rs.getInt("user_id"),
                            name = rs.getString("name"),
                            email = rs.getString("email"),
                            password_hash = rs.getString("password_hash"),
                            phone = rs.getString("phone"),
                            role = rs.getString("role"),
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

    private fun updateUser() {
        val user = selectedUser ?: run {
            Toast.makeText(this, "Vui lòng chọn người dùng để sửa", Toast.LENGTH_SHORT).show()
            return
        }
        val name = etUserName.text.toString().trim()
        val email = etUserEmail.text.toString().trim()
        val password = etUserPassword.text.toString().trim()
        val phone = etUserPhone.text.toString().trim()
        val role = etUserRole.text.toString().trim()
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "UPDATE Users SET name=?, email=?, password_hash=?, phone=?, role=? WHERE user_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setString(1, name)
                pstmt.setString(2, email)
                pstmt.setString(3, password)
                pstmt.setString(4, phone)
                pstmt.setString(5, role)
                pstmt.setInt(6, user.user_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadUsers()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun deleteUser() {
        val user = selectedUser ?: return
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "DELETE FROM Users WHERE user_id=?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, user.user_id)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadUsers()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi xóa: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 