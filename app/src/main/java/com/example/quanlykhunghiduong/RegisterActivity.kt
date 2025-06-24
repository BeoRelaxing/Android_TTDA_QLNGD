package com.example.quanlykhunghiduong

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.sql.DriverManager
import java.util.concurrent.Executors

class RegisterActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnRegister: Button

    private val executor = Executors.newSingleThreadExecutor()

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etPhone = findViewById(R.id.etPhone)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "INSERT INTO Users (name, email, password_hash, phone, role, created_at) VALUES (?, ?, ?, ?, 'customer', GETDATE())"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setString(1, name)
                pstmt.setString(2, email)
                pstmt.setString(3, password)
                pstmt.setString(4, phone)
                pstmt.executeUpdate()
                pstmt.close()
                conn.close()
                runOnUiThread {
                    Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 