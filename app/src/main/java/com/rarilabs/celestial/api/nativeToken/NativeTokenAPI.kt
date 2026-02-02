package com.rarilabs.celestial.api.nativeToken


import com.rarilabs.celestial.api.nativeToken.models.TransactionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NativeTokenAPI {
    @GET("/api/v2/addresses/{address_hash}/transactions")
    suspend fun getTransactions(@Path("address_hash") addressHash: String): Response<TransactionResponse>

}