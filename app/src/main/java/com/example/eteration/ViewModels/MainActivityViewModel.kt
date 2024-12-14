package com.example.eteration.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eteration.Models.FilterModel
import com.example.eteration.Models.Package
import com.example.eteration.Models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appViewModel: AppViewModel
): ViewModel() {

    private val _filterProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _filterItems = MutableStateFlow<MutableMap<String, Set<String>>>(mutableMapOf())
    private var _filterItem = MutableStateFlow(FilterModel())
    private val _isFilterVisible = MutableStateFlow(false)
    private val _isSortPriceFromBigToSmall = MutableStateFlow(true)
    private val _searchText = MutableStateFlow("")

    val packages: StateFlow<List<Package>> = appViewModel.packages
    val filterProducts: StateFlow<List<Product>> = _filterProducts.asStateFlow()
    val filterItems: StateFlow<MutableMap<String, Set<String>>> = _filterItems.asStateFlow()
    val filterItem: StateFlow<FilterModel> = _filterItem.asStateFlow()
    val isFilterVisible: StateFlow<Boolean> = _isFilterVisible.asStateFlow()
    val isSortPriceFromBigToSmall: StateFlow<Boolean> = _isSortPriceFromBigToSmall.asStateFlow()
    val searchText: StateFlow<String> = _searchText.asStateFlow()
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {

        viewModelScope.launch {
            launch {
                appViewModel.productList.collect {
                    updateFilterProducts()
                    updateFilterItems(it)
                }
            }

            launch {
                _filterItem.collect {
                    updateFilterProducts()
                }
            }

            launch {
                _searchText.collect {
                    updateFilterProducts()
                }
            }

            launch {
                appViewModel.isLoading.collect {
                    isLoading.value = it
                }
            }
        }
    }

    private fun updateFilterItems(productList: List<Product>) {
        val items = mutableMapOf<String, Set<String>>()

        productList.forEach { product ->

            var setList = items[product.brand]
            setList = setList?.plus(product.model) ?: setOf(product.model)
            items[product.brand] = setList
        }

       _filterItems.value = items
    }

    fun updatePackagesList(product: Product, isAdd: Boolean) {

        viewModelScope.launch {
            appViewModel.addPackagesList(product)
        }
    }

    fun updatePackagesList(packageItem: Package, isAdd: Boolean) {
        viewModelScope.launch {
            if (isAdd) {
                appViewModel.addPackagesList(packageItem)
            } else {
                appViewModel.removePackagesList(packageItem)
            }
        }
    }

    fun toggleFilterVisibility() {
        _isFilterVisible.value = !(_isFilterVisible.value)
    }

    fun setFilterModel(filterModel: FilterModel) {
        _filterItem.value = filterModel
    }

    fun filterSearchText(string: String) {
        _searchText.value = string
    }

    fun toggleSortPrice() {
        _isSortPriceFromBigToSmall.value = !(_isSortPriceFromBigToSmall.value)
        priceSort()
    }


    private fun searchFilterUpdate() {
        if (searchText.value.isEmpty())
            return
        val filteredList = _filterProducts.value.filter { it.model.contains(searchText.value, ignoreCase = true) || it.brand.contains(searchText.value, ignoreCase = true) || it.price.contains(searchText.value, ignoreCase = true) || it.name.contains(searchText.value, ignoreCase = true) }
        _filterProducts.value = filteredList
    }

    private fun brandAndModelFilterUpdate() {
        if (filterItem.value.brand.isEmpty() || filterItem.value.brand.equals("ALL")) {
            _filterProducts.value = appViewModel.productList.value
            return
        }
        if (filterItem.value.model.equals("ALL")) {
            val filteredList = appViewModel.productList.value.filter { it.brand.contains(filterItem.value.brand, ignoreCase = true) }
            _filterProducts.value = filteredList
        }
        val filteredList = appViewModel.productList.value.filter { it.brand.contains(filterItem.value.brand, ignoreCase = true) && it.model.contains(filterItem.value.model, ignoreCase = true) }
        _filterProducts.value = filteredList
    }

    private fun priceFilter() {
        if (filterItem.value.lowPrice.isEmpty() && filterItem.value.bigPrice.isEmpty())
            return
        if (filterItem.value.lowPrice.isEmpty()) {
            val big = filterItem.value.bigPrice.toDoubleOrNull() ?: Double.MIN_VALUE
            val filteredList = appViewModel.productList.value.filter {
                (it.price.toDoubleOrNull() ?: 0.0) <= big
            }
            _filterProducts.value = filteredList
        }
        if (filterItem.value.bigPrice.isEmpty()) {
            val low = filterItem.value.lowPrice.toDoubleOrNull() ?: Double.MIN_VALUE
            val filteredList = appViewModel.productList.value.filter {
                (it.price.toDoubleOrNull() ?: 0.0) <= low
            }
            _filterProducts.value = filteredList
        }

        val low = filterItem.value.lowPrice.toDoubleOrNull() ?: Double.MIN_VALUE
        val high = filterItem.value.bigPrice.toDoubleOrNull() ?: Double.MAX_VALUE
        val filteredList = appViewModel.productList.value.filter {
            (it.price.toDoubleOrNull() ?: 0.0) in low..high
        }
        _filterProducts.value = filteredList
    }

    private fun priceSort() {
        if (isSortPriceFromBigToSmall.value) {
            _filterProducts.value = _filterProducts.value.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
        } else {
            _filterProducts.value = _filterProducts.value.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
        }
    }

    private fun updateFilterProducts() {
        brandAndModelFilterUpdate()
        priceFilter()
        searchFilterUpdate()
        priceSort()
    }

    fun filterItemClear() {
        _filterItem.value = FilterModel()
    }
}