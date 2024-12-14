package com.example.eteration.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eteration.App.AppModule
import com.example.eteration.Models.Package
import com.example.eteration.Models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val appModul: AppModule
): ViewModel() {

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    private val _packages = MutableStateFlow<List<Package>>(emptyList())
    private val _loading = MutableStateFlow<Boolean>(false)
    val productList: StateFlow<List<Product>> = _productList.asStateFlow()
    val packages: StateFlow<List<Package>> = _packages.asStateFlow()
    val isLoading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        fetchProducts()
        fetchAllPackages()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            while (true) {
                try {
                    _loading.value = true
                    val deferredResponse = async { appModul.provideApi().getExampleData() }
                    val result = deferredResponse.await()
                    _loading.value = false
                    _productList.value = result
                    val productsToRemove = _packages.value.filter { savedProduct ->
                        _productList.value.none { newProduct -> newProduct.id == savedProduct.id }
                    }
                    val productsToRemoveIds = productsToRemove.map { it.id }
                    productsToRemoveIds.forEach {
                        removeByIdPackageLD(it)
                    }
                    val updatePackages = _packages.value.filter { savedProduct ->
                        !productsToRemove.contains(savedProduct)
                    }
                    _packages.value = updatePackages
                } catch (e: Exception) {
                    _productList.value = emptyList()
                }
                delay(600000L)
            }
        }
    }

    private fun fetchAllPackages() {
        viewModelScope.launch {
            _packages.value = appModul.providePackageDao().getAllPackages()
        }
    }

    suspend fun addPackagesList(product: Product) {

        val value = packages.value.find { it.id == product.id }
        if (value != null) {
            val newList = packages.value.map {
                if (it.id == value.id) {
                    val updatedPackage = it.copy(count = it.count + 1)
                    addPackageLD(updatedPackage)
                    updatedPackage
                } else {
                    it
                }
            }.toMutableList()
            _packages.value = newList
            return
        }
        val packageItem = Package(product.id, product.price, product.name, 1)
        val newList = packages.value.toMutableList()
        newList.add(packageItem) // yeni öğeyi ekliyoruz
        _packages.value = newList
        addPackageLD(packageItem)
    }

    suspend fun addPackagesList(packageItem: Package) {
        val value = packages.value.find { it.id == packageItem.id }

        if (value == null) {
            val newList = packages.value.toMutableList()
            newList.add(packageItem)
            _packages.value = newList
        } else {
            val newList = packages.value.map {
                if (it.id == value.id) {
                    it.copy(count = packageItem.count)
                } else {
                    it
                }
            }.toMutableList()
            _packages.value = newList
        }
        addPackageLD(packageItem)
    }

    suspend fun removePackagesList(packageItem: Package) {

        val value = packages.value.find { it.id == packageItem.id }
        val newList: MutableList<Package>

        if (value == null) {
            removePackageLD(packageItem)
        } else {
            if (packageItem.count >= 1) {
                newList = packages.value.map {
                    if (it.id == value.id) {
                        it.copy(count = it.count)
                    } else {
                        it
                    }
                }.toMutableList()
                _packages.value = newList
                addPackageLD(packageItem)
                return
            }
            newList = packages.value.toMutableList()
            newList.remove(value)
            _packages.value = newList
            removePackageLD(packageItem)
        }
    }

    private suspend fun addPackageLD(packageItem: Package) {
        appModul.providePackageDao().insertPackage(packageItem)
    }


    private suspend fun removePackageLD(packageItem: Package) {
        appModul.providePackageDao().deletePackage(packageItem)
    }

    private suspend fun removeByIdPackageLD(id: String) {
        val packageItem = appModul.providePackageDao().getPackageById(id)
        if (packageItem != null)
            removePackageLD(packageItem)
    }
}