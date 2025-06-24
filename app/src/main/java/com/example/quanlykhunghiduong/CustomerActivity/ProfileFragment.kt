package com.example.quanlykhunghiduong.CustomerActivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quanlykhunghiduong.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.sql.DriverManager
import java.util.concurrent.Executors

class ProfileFragment : Fragment() {
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var updateButton: MaterialButton
    private lateinit var currentPasswordEditText: TextInputEditText
    private lateinit var newPasswordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var changePasswordButton: MaterialButton

    private val executor = Executors.newSingleThreadExecutor()
    private var userId: Int = -1

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
        private const val TAG = "ProfileFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lấy userId từ intent
        userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
        Log.d(TAG, "ProfileFragment được tạo với userId: $userId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo các view
        nameEditText = view.findViewById(R.id.name_edit_text)
        emailEditText = view.findViewById(R.id.email_edit_text)
        phoneEditText = view.findViewById(R.id.phone_edit_text)
        updateButton = view.findViewById(R.id.update_button)
        currentPasswordEditText = view.findViewById(R.id.current_password_edit_text)
        newPasswordEditText = view.findViewById(R.id.new_password_edit_text)
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text)
        changePasswordButton = view.findViewById(R.id.change_password_button)

        // Kiểm tra userId
        if (userId == -1) {
            // Thử lấy userId từ intent một lần nữa
            userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
            Log.d(TAG, "Thử lấy userId lần nữa: $userId")
        }

        if (userId == -1) {
            Log.e(TAG, "Không nhận được userId")
            Toast.makeText(context, "Lỗi: Không thể tải thông tin người dùng", Toast.LENGTH_LONG).show()
            return
        }

        // Load thông tin người dùng
        loadUserInfo()

        // Xử lý sự kiện cập nhật thông tin
        updateButton.setOnClickListener {
            Log.d(TAG, "Nút cập nhật thông tin được ấn")
            updateUserInfo()
        }

        // Xử lý sự kiện đổi mật khẩu
        changePasswordButton.setOnClickListener {
            Log.d(TAG, "Nút đổi mật khẩu được ấn")
            changePassword()
        }
    }

    override fun onResume() {
        super.onResume()
        // Kiểm tra userId trước khi load thông tin
        if (userId != -1) {
            loadUserInfo()
        } else {
            // Thử lấy userId từ intent một lần nữa
            userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
            if (userId != -1) {
                loadUserInfo()
            } else {
                Log.e(TAG, "Không thể lấy userId trong onResume")
            }
        }
    }

    private fun loadUserInfo() {
        Log.d(TAG, "Bắt đầu load thông tin người dùng với userId: $userId")
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "SELECT name, email, phone FROM Users WHERE user_id = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setInt(1, userId)
                val rs = pstmt.executeQuery()

                if (rs.next()) {
                    val name = rs.getString("name")
                    val email = rs.getString("email")
                    val phone = rs.getString("phone")

                    Log.d(TAG, "Đã lấy được thông tin: name=$name, email=$email, phone=$phone")

                    activity?.runOnUiThread {
                        nameEditText.setText(name)
                        emailEditText.setText(email)
                        phoneEditText.setText(phone)
                    }
                } else {
                    Log.e(TAG, "Không tìm thấy thông tin người dùng với userId: $userId")
                }

                rs.close()
                pstmt.close()
                conn.close()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi tải thông tin người dùng", e)
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateUserInfo() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        Log.d(TAG, "Bắt đầu cập nhật thông tin: name=$name, email=$email, phone=$phone")

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val sql = "UPDATE Users SET name = ?, email = ?, phone = ? WHERE user_id = ?"
                val pstmt = conn.prepareStatement(sql)
                pstmt.setString(1, name)
                pstmt.setString(2, email)
                pstmt.setString(3, phone)
                pstmt.setInt(4, userId)
                val result = pstmt.executeUpdate()

                Log.d(TAG, "Kết quả cập nhật: $result dòng bị ảnh hưởng")

                activity?.runOnUiThread {
                    Toast.makeText(context, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                }

                pstmt.close()
                conn.close()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi cập nhật thông tin", e)
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun changePassword() {
        val currentPassword = currentPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        Log.d(TAG, "Bắt đầu đổi mật khẩu")

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(context, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)

                // Kiểm tra mật khẩu hiện tại
                val checkSql = "SELECT password_hash FROM Users WHERE user_id = ? AND password_hash = ?"
                val checkPstmt = conn.prepareStatement(checkSql)
                checkPstmt.setInt(1, userId)
                checkPstmt.setString(2, currentPassword)
                val rs = checkPstmt.executeQuery()

                if (rs.next()) {
                    // Cập nhật mật khẩu mới
                    val updateSql = "UPDATE Users SET password_hash = ? WHERE user_id = ?"
                    val updatePstmt = conn.prepareStatement(updateSql)
                    updatePstmt.setString(1, newPassword)
                    updatePstmt.setInt(2, userId)
                    val result = updatePstmt.executeUpdate()

                    Log.d(TAG, "Kết quả đổi mật khẩu: $result dòng bị ảnh hưởng")

                    activity?.runOnUiThread {
                        Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                        // Xóa các trường nhập liệu
                        currentPasswordEditText.text?.clear()
                        newPasswordEditText.text?.clear()
                        confirmPasswordEditText.text?.clear()
                    }
                } else {
                    Log.d(TAG, "Mật khẩu hiện tại không đúng")
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
                    }
                }

                rs.close()
                checkPstmt.close()
                conn.close()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi đổi mật khẩu", e)
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 