package com.example.eteration.Interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eteration.Models.Package
import com.example.eteration.Models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(user: Package)

    @Insert
    suspend fun insertAll(packages: List<Package>)

    @Query("SELECT * FROM packages WHERE id = :id")
    suspend fun getPackageById(id: String): Package?

    @Query("DELETE FROM packages")
    suspend fun deleteAllProducts()

    @Query("SELECT * FROM packages")
    suspend fun getAllPackages(): List<Package>

    @Delete
    suspend fun deletePackage(user: Package)
}