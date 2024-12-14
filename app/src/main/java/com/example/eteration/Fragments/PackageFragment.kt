package com.example.eteration.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eteration.Adapters.PackagesAdapter
import com.example.eteration.Adapters.ProductsAdapter
import com.example.eteration.Models.Package
import com.example.eteration.R
import com.example.eteration.ViewModels.MainActivityViewModel
import com.example.eteration.databinding.FragmentPackageBinding
import kotlinx.coroutines.launch

class PackageFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentPackageBinding
    private var packageAdapter: PackagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPackageBinding.inflate(inflater, container, false)
        return  binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            launch {
                mainActivityViewModel.packages.collect { packageList ->
                    if (packageAdapter == null) {
                        createPackageAdapter(packageList)
                    } else {
                        packageAdapter!!.updateData(packageList)
                    }
                    binding.totalText.text = packageList.sumOf { it.price.toDouble() * it.count }.toString()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createPackageAdapter(packageList: List<Package>) {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        binding.packageRecyclerView.layoutManager = gridLayoutManager
        binding.packageRecyclerView.adapter = PackagesAdapter(packageList) { packageItem, isPlus ->
            if (isPlus) {
                mainActivityViewModel.updatePackagesList(packageItem, true)
                binding.totalText.text = (binding.totalText.text.toString().toDouble() + packageItem.price.toDouble()).toString()
                return@PackagesAdapter
            }
            mainActivityViewModel.updatePackagesList(packageItem, false)
            binding.totalText.text = (binding.totalText.text.toString().toDouble() - packageItem.price.toDouble()).toString()
        }
    }
}