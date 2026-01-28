package com.grnddsystems.celestials.api.nativeToken


import com.grnddsystems.celestials.api.nativeToken.models.TransactionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NativeTokenAPI {
    @GET("/api/v2/addresses/{address_hash}/transactions")
    suspend fun getTransactions(@Path("address_hash") addressHash: String): Response<TransactionResponse>

}