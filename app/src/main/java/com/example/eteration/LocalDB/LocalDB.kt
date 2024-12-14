package com.example.eteration.LocalDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eteration.Interfaces.PackageDao
import com.example.eteration.Models.Package

@Database(entities = [Package::class], version = 1, exportSchema = true)
abstract class LocalDB: RoomDatabase() {
    abstract fun packageDao(): PackageDao
}
