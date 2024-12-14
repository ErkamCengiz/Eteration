package com.example.eteration.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.eteration.Models.Package
import com.example.eteration.Utility.DiffUtilCallback
import com.example.eteration.databinding.PackageAdapterLayoutBinding

class PackagesAdapter(var list: List<Package>, val onItemCountClick: (packageItem: Package, isPlus: Boolean) -> Unit): RecyclerView.Adapter<PackagesAdapter.PackageViewHolder>() {

    inner class PackageViewHolder(private val binding: PackageAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(packageItem: Package) {
            binding.productNameText.text = packageItem.name
            binding.counterText.text = packageItem.count.toString()
            binding.productPriceText.text = (packageItem.count * packageItem.price.toDouble()).toString()
            binding.incrementButton.setOnClickListener {
                packageItem.count += 1
                binding.productPriceText.text = (packageItem.count * packageItem.price.toDouble()).toString()
                binding.counterText.text = packageItem.count.toString()
                onItemCountClick(packageItem, true)
            }
            binding.decrementButton.setOnClickListener {
                packageItem.count -= 1
                binding.productPriceText.text = (packageItem.count * packageItem.price.toDouble()).toString()
                binding.counterText.text = packageItem.count.toString()
                onItemCountClick(packageItem, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = PackageAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PackageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageItem = list.get(position)
        holder.bind(packageItem)
    }

    fun updateData(newItems: List<Package>) {
        val diffCallback = DiffUtilCallback(newItems, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // products listesini doğrudan yeni listeyle güncelle
        list = newItems

        // Yeni listeyi RecyclerView'a uygulamak
        diffResult.dispatchUpdatesTo(this)
    }
}