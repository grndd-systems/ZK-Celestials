package com.rarilabs.celestial.api.registration

import com.rarilabs.celestial.api.registration.models.RegisterBody
import com.rarilabs.celestial.api.registration.models.RegisterResponseBody
import com.rarilabs.celestial.api.registration.models.VerifySodRequest
import com.rarilabs.celestial.api.registration.models.VerifySodResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationAPI {
    @POST("/integrations/registration-relayer/v1/register")
    suspend fun register(@Body body: RegisterBody): Response<RegisterResponseBody>

    @POST("/integrations/incognito-light-registrator/v1/register")
    suspend fun incognitoLightRegistrator(@Body body: VerifySodRequest): Response<VerifySodResponse>
}