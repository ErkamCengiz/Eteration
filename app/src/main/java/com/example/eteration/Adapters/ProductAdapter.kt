package com.example.eteration.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.eteration.Models.Product
import com.example.eteration.Utility.DiffUtilCallback
import com.example.eteration.Utility.DisplayUtils
import com.example.eteration.Utility.UrlFotoDownload
import com.example.eteration.databinding.ProductsAdapterLayoutBinding

class ProductsAdapter(var products: List<Product>, val onItemClick: (Product) -> Unit, val onAddPackageClick: (Product) -> Unit): RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    inner class ProductsViewHolder(private val binding: ProductsAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            UrlFotoDownload.urlFotoSetImage(binding.productImageView, product.image)
            binding.productNameText.text = product.name
            binding.productPriceText.text = product.price
            binding.root.setOnClickListener { onItemClick(product) }
            binding.addPackageButton.setOnClickListener {
                onAddPackageClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val binding = ProductsAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductsViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = products.get(position)
        holder.bind(product)
        onItemClick

        if(products.size % 2 == 0) {
            if (position == products.size - 2 || position == products.size -1) {
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = DisplayUtils.getScreenHeight() * 2/10
                holder.itemView.layoutParams = layoutParams
            } else {
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = 0
                holder.itemView.layoutParams = layoutParams
            }
        } else {
            if (position == products.size -1) {
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = DisplayUtils.getScreenHeight() * 2/10
                holder.itemView.layoutParams = layoutParams
            } else {
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = DisplayUtils.getScreenHeight() * 2/10
                holder.itemView.layoutParams = layoutParams
            }
        }
    }

    fun updateData(newItems: List<Product>) {
        val diffCallback = DiffUtilCallback(products, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // products listesini doğrudan yeni listeyle güncelle
        products = newItems

        // Yeni listeyi RecyclerView'a uygulamak
        diffResult.dispatchUpdatesTo(this)
    }
}