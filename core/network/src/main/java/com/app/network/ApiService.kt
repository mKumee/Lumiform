package com.app.network

import com.app.network.data.ItemDto
import retrofit2.http.GET


interface ApiService {
    @GET("aruana-lumiform/383e1291df3e0cc1d49aeb14d45f5b94/raw/lumiform-android-test.json")
    suspend fun getResponseApi(): List<ItemDto>
}
