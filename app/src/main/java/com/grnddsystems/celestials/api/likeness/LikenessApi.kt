package com.grnddsystems.celestials.api.likeness

import com.grnddsystems.celestials.api.likeness.models.LikenessRequest
import com.grnddsystems.celestials.api.likeness.models.LikenessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LikenessApi {

    @POST("/integrations/registration-relayer/v1/likeness-registry")
    suspend fun likenessRegistry(@Body request: LikenessRequest): Response<LikenessResponse>

}