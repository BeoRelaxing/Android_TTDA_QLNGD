package com.example.quanlykhunghiduong.CustomerActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.quanlykhunghiduong.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var userId: Int = -1

    companion object {
        private const val TAG = "CustomerActivity"
        const val EXTRA_USER_ID = "USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        // Lấy userId từ intent
        userId = intent.getIntExtra(EXTRA_USER_ID, -1)
        Log.d(TAG, "CustomerActivity được khởi tạo với userId: $userId")

        if (userId == -1) {
            Log.e(TAG, "Không nhận được userId từ intent")
            finish()
            return
        }

        // Thiết lập NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Thiết lập BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Thêm listener để truyền userId khi chuyển fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "Chuyển đến fragment: ${destination.label}")
            // Tìm fragment hiện tại và truyền userId
            val currentFragment = supportFragmentManager.findFragmentById(destination.id)
            if (currentFragment != null) {
                val intent = Intent().apply {
                    putExtra(EXTRA_USER_ID, userId)
                }
                currentFragment.arguments = intent.extras
                Log.d(TAG, "Đã truyền userId: $userId đến fragment: ${destination.label}")
            }
        }
    }
} 