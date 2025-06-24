package com.example.quanlykhunghiduong.AdminActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlykhunghiduong.R

class AdminMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        val btnResortManager = findViewById<Button>(R.id.btnResortManager)
        btnResortManager.setOnClickListener {
            startActivity(Intent(this, AdminResortActivity::class.java))
        }
        val btnRoomManager = findViewById<Button>(R.id.btnRoomManager)
        btnRoomManager.setOnClickListener {
            startActivity(Intent(this, AdminRoomActivity::class.java))
        }

        val btnServiceManager = findViewById<Button>(R.id.btnServiceManager)
        btnServiceManager.setOnClickListener {
            startActivity(Intent(this, AdminServiceActivity::class.java))
        }

        val btnBookingManager = findViewById<Button>(R.id.btnBookingManager)
        btnBookingManager.setOnClickListener {
            startActivity(Intent(this, CustomerBookingActivity::class.java))
        }

        val btnUserManager = findViewById<Button>(R.id.btUserManager)
        btnUserManager.setOnClickListener {
            startActivity(Intent(this, AdminUserActivity::class.java))
        }

        val btnBKSVManager = findViewById<Button>(R.id.btBKSVManager)
        btnBKSVManager.setOnClickListener {
            startActivity(Intent(this, CustomerServiceBookingActivity::class.java))
        }

        val btntbManager = findViewById<Button>(R.id.bttbManager)
        btntbManager.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        val btnfbManager = findViewById<Button>(R.id.btfbManager)
        btnfbManager.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
    }
}

