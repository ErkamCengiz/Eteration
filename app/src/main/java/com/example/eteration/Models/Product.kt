package com.example.eteration.Models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(val id: String, var brand: String, var model: String, var description: String, var price: String, var image: String, var name: String, var createdAt: String): Parcelable