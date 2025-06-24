package com.example.quanlykhunghiduong

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhunghiduong.AdminActivity.AdminMainActivity
import com.example.quanlykhunghiduong.CustomerActivity.CustomerActivity
import java.sql.DriverManager
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private val executor = Executors.newSingleThreadExecutor()

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etName = findViewById(R.id.etName)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            Log.d(TAG, "Nút đăng nhập được ấn")
            login()
        }
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val name = etName.text.toString().trim()
        val password = etPassword.text.toString().trim()

        Log.d(TAG, "Bắt đầu login với name: $name, password: $password")

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Thiếu thông tin đăng nhập")
            return
        }

        executor.execute {
            try {
                Log.d(TAG, "Đang kết nối DB...")
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                Log.d(TAG, "Kết nối DB thành công")
                val sql = "SELECT * FROM Users WHERE name = ? AND password_hash = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setString(1, name)
                pstmt.setString(2, password)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    val role = rs.getString("role")
                    val userId = rs.getInt("user_id")
                    Log.d(TAG, "Đăng nhập thành công, role: $role, userId: $userId")
                    runOnUiThread {
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        if (role == "admin") {
                            startActivity(Intent(this, AdminMainActivity::class.java))
                        } else {
                            val intent = Intent(this, CustomerActivity::class.java)
                            intent.putExtra(CustomerActivity.EXTRA_USER_ID, userId) // Dùng hằng số
                            Log.d(TAG, "Chuyển đến CustomerActivity với userId: $userId")
                            startActivity(intent)
                        }
                        finish()
                    }
                } else {
                    Log.d(TAG, "Sai tên đăng nhập hoặc mật khẩu!")
                    runOnUiThread {
                        Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
                    }
                }
                rs.close()
                pstmt.close()
                conn.close()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi đăng nhập", e)
                runOnUiThread {
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 