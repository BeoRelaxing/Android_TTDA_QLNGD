package com.example.quanlykhunghiduong.CustomerActivity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quanlykhunghiduong.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerResortMenuActivity : AppCompatActivity() {
    private var resortId: Int = -1
    private var resortName: String = ""
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_resort_menu)

        resortId = intent.getIntExtra("RESORT_ID", -1)
        resortName = intent.getStringExtra("RESORT_NAME") ?: ""
        userId = intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)

        findViewById<TextView>(R.id.tvResortName).text = resortName

        val bottomNav = findViewById<BottomNavigationView>(R.id.resort_menu_bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_booking -> {
                    openFragment(CustomerBookingFragment())
                    true
                }
                R.id.menu_service -> {
                    openFragment(CustomerServiceFragment())
                    true
                }
                R.id.menu_invoice -> {
                    openFragment(CustomerInvoiceFragment())
                    true
                }
                else -> false
            }
        }
        // Mặc định mở tab Đặt phòng
        bottomNav.selectedItemId = R.id.menu_booking
    }

    private fun openFragment(fragment: Fragment) {
        val bundle = Bundle().apply {
            putInt("RESORT_ID", resortId)
            putString("RESORT_NAME", resortName)
            putInt(CustomerActivity.EXTRA_USER_ID, userId)
        }
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.resort_menu_nav_host, fragment)
            .commit()
    }
} 