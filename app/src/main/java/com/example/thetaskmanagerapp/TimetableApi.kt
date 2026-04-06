package com.example.thetaskmanagerapp

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface TimetableApi {
    @Headers("Content-Type: application/json")
    @POST("r1/reservation/search")
    suspend fun getTimetable(
        @Header("Authorization") authHeader: String,
        @Body request: TimetableRequest
    ): TimetableResponse
}
