package com.example.eteration.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packages")
data class Package(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    var price: String,
    var name: String,
    var count: Int
)