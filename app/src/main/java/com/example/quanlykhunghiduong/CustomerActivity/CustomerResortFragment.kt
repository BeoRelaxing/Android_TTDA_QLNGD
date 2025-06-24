package com.example.quanlykhunghiduong.CustomerActivity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlykhunghiduong.R
import com.example.quanlykhunghiduong.models.Resort
import java.sql.DriverManager
import java.util.concurrent.Executors

class CustomerResortFragment : Fragment() {
    private lateinit var etSearch: EditText
    private lateinit var rvResorts: RecyclerView
    private lateinit var resortAdapter: CustomerResortAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private var allResorts: List<Resort> = emptyList()

    companion object {
        private const val DB_URL = "jdbc:jtds:sqlserver://10.0.2.2:1433/QuanLyKhuNghiDuong"
        private const val DB_USER = "sa"
        private const val DB_PASS = "03092003"
        private const val TAG = "CustomerResortFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_resort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSearch = view.findViewById(R.id.etSearchCustomerResort)
        rvResorts = view.findViewById(R.id.rvCustomerResorts)
        resortAdapter = CustomerResortAdapter(emptyList()) { resort ->
            openResortMenu(resort)
        }
        rvResorts.layoutManager = LinearLayoutManager(context)
        rvResorts.adapter = resortAdapter
        loadResorts()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResorts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadResorts() {
        executor.execute {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM Resorts")
                val resorts = mutableListOf<Resort>()
                while (resultSet.next()) {
                    resorts.add(
                        Resort(
                            resort_id = resultSet.getInt("resort_id"),
                            name = resultSet.getString("name"),
                            location = resultSet.getString("location"),
                            type = resultSet.getString("type"),
                            description = resultSet.getString("description"),
                            price_range = resultSet.getString("price_range"),
                            amenities = resultSet.getString("amenities"),
                            created_at = resultSet.getString("created_at")
                        )
                    )
                }
                resultSet.close()
                statement.close()
                connection.close()
                allResorts = resorts
                activity?.runOnUiThread {
                    resortAdapter.updateData(resorts)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi load resort: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Lỗi: Không thể tải danh sách khu nghỉ dưỡng", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun filterResorts(query: String) {
        val filtered = allResorts.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true)
        }
        resortAdapter.updateData(filtered)
    }

    private fun openResortMenu(resort: Resort) {
        val intent = Intent(requireContext(), CustomerResortMenuActivity::class.java)
        intent.putExtra("RESORT_ID", resort.resort_id)
        intent.putExtra("RESORT_NAME", resort.name)
        val userId = requireActivity().intent.getIntExtra(CustomerActivity.EXTRA_USER_ID, -1)
        intent.putExtra(CustomerActivity.EXTRA_USER_ID, userId)
        startActivity(intent)
    }
}