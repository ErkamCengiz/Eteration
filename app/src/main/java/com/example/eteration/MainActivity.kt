package com.example.eteration

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.eteration.Models.FilterModel
import com.example.eteration.ViewModels.MainActivityViewModel
import com.example.eteration.databinding.ActivityMainBinding
import com.example.eteration.databinding.FragmentFilterBinding
import com.example.eteration.databinding.ListPopupViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createBottomNavigation()

        lifecycleScope.launch {
            viewModel.isFilterVisible.collectLatest { shouldShow ->
                if (shouldShow) {
                    filterPopupShow()
                }
            }
        }
    }

    private fun filterPopupShow() {
        val popupBinding = FragmentFilterBinding.inflate(LayoutInflater.from(this))

        val popupWindow = PopupWindow(
            popupBinding.root,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            true
        )

        // Popup'un arka planını belirle (şeffaf arka plan için gerekli)
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent))
        // Popup'u bir anchor view'e göre göster
        popupWindow.showAsDropDown(binding.root, 0, 10)

        popupBinding.brandButton.setOnClickListener {
            val arrayList: ArrayList<String> = ArrayList(viewModel.filterItems.value.keys.toList())
            if (arrayList.size == 0) {
                Toast.makeText(this, "Dont Have Item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            arrayList.add(0, "ALL")
            showPopup("Brand Select", arrayList) {
                popupBinding.brandText.text = it
                if (it.equals("ALL")) {
                    popupBinding.modelText.text = "ALL"
                } else {
                    popupBinding.modelText.text = ""
                }
            }
        }

        popupBinding.modelButton.setOnClickListener {
            if (popupBinding.brandText.text.isEmpty()) {
                Toast.makeText(this, "Please Select Brand", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val arrayList: ArrayList<String> = viewModel.filterItems.value[popupBinding.brandText.text]?.let { it1 -> ArrayList(it1) } ?: ArrayList()
            if (arrayList.size == 0) {
                Toast.makeText(this, "Dont Have Item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            arrayList.add(0, "ALL")
            viewModel.filterItems.value[popupBinding.brandText.text]?.let { it1 ->
                showPopup("Model Select" ,it1.toMutableList()) {
                    popupBinding.modelText.text = it
                }
            }
        }

        popupBinding.cancelButton.setOnClickListener {
            popupWindow.dismiss()
        }

        popupBinding.okeyButton.setOnClickListener {
            viewModel.setFilterModel(FilterModel(popupBinding.brandText.text.toString(), popupBinding.modelText.text.toString(), popupBinding.firstPrice.text.toString(), popupBinding.secondPrice.text.toString()))
            popupWindow.dismiss()
        }
    }

    private fun showPopup(title: String, items: List<String>, onItemClick: (String) -> Unit) {
        val popupBinding = ListPopupViewBinding.inflate(LayoutInflater.from(this))

        val popupWindow = PopupWindow(
            popupBinding.root,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            true
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        popupBinding.title.text = title

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        popupBinding.listView.adapter = adapter

        popupBinding.listView.setOnItemClickListener { _, _, position, _ ->
            onItemClick(items[position])
            popupWindow.dismiss()
        }

        popupBinding.backButton.setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
    }

    fun createBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)
    }
}