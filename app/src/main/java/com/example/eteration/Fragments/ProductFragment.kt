package com.example.eteration.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.eteration.Adapters.ProductsAdapter
import com.example.eteration.Models.Package
import com.example.eteration.Models.Product
import com.example.eteration.R
import com.example.eteration.ViewModels.MainActivityViewModel
import com.example.eteration.databinding.FragmentProductBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentProductBinding;
    private var productAdapter: ProductsAdapter? = null
    private var filterNotNull: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            launch {
                mainActivityViewModel.filterProducts.collect {
                    if(productAdapter == null)
                        createAdapter(it)
                    else
                        productAdapter!!.updateData(it)
                }
            }

            launch {
                mainActivityViewModel.filterItem.collect {
                    if (it.brand.isNotEmpty() && it.model.isNotEmpty()) {
                        binding.filterImageView.setImageResource(R.drawable.baseline_close_24)
                        filterNotNull = true
                    } else {
                        binding.filterImageView.setImageResource(R.drawable.filter_picture)
                        filterNotNull = false
                    }
                }
            }

            launch {
                mainActivityViewModel.packages.collect {
                    var count = 0
                    it.forEach {
                        count += it.count
                    }
                    binding.packageItemCountText.text = count.toString()
                }
            }

            launch {
                mainActivityViewModel.isLoading.collect {
                    if (it) {
                        binding.loadingProgress.visibility = View.VISIBLE
                        return@collect
                    }
                    binding.loadingProgress.visibility = View.GONE
                }
            }

            launch {
                mainActivityViewModel.filterItem.collect {
                    if (it.brand.isEmpty() && it.model.isEmpty() && it.lowPrice.isEmpty() && it.bigPrice.isEmpty())
                        binding.filterImageView.setImageResource(R.drawable.filter_picture)
                    else
                        binding.filterImageView.setImageResource(R.drawable.baseline_close_24)
                }
            }
        }

        binding.filterButton.setOnClickListener {
            val filterItem = mainActivityViewModel.filterItem.value
            if (filterItem.model.isEmpty() && filterItem.brand.isEmpty() && filterItem.bigPrice.isEmpty() && filterItem.lowPrice.isEmpty()) {
                mainActivityViewModel.toggleFilterVisibility()
                return@setOnClickListener
            }
            mainActivityViewModel.filterItemClear()

        }

        binding.sortPriceButton.setOnClickListener {
            mainActivityViewModel.toggleSortPrice()
            if (mainActivityViewModel.isSortPriceFromBigToSmall.value) {
                binding.priceImage.setImageResource(R.drawable.bottom_arrow)
            } else {
                binding.priceImage.setImageResource(R.drawable.up_arrow_picture)
            }
        }

        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    binding.searchClearButton.visibility = View.GONE
                } else {
                    binding.searchClearButton.visibility = View.VISIBLE
                }
                mainActivityViewModel.filterSearchText(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchClearButton.setOnClickListener {
            binding.searchText.text.clear()
        }
    }

    private fun createAdapter(products: List<Product>) {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.adapter = ProductsAdapter(products,
            {selectedProduct ->
            val action = ProductFragmentDirections.actionProductFragmentToDetailFragment(selectedProduct)
            findNavController().navigate(action)
            },
            { addProduct ->
                mainActivityViewModel.updatePackagesList(addProduct, true)
            }
        )
    }
}