package com.devtrack

import retrofit2.http.GET

interface ApiService {
    @GET("cpu")
    suspend fun getCpu(): CpuResponse

    @GET("ram")
    suspend fun getRam(): Map<String, String>

    @GET("disk")
    suspend fun getDisk(): Map<String, String>
}