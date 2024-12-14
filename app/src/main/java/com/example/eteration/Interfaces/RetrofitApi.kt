package com.example.eteration.Interfaces

import com.example.eteration.Models.Product
import retrofit2.http.GET

interface RetrofitApi {

    @GET("products")
    suspend fun getExampleData(): List<Product>
}