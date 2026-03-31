package com.example.thetaskmanagerapp

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TimetableApi {
    @POST("r1/reservation/search")
    suspend fun getTimetable(
        @Header("Authorization") authHeader: String,
        @Body request: TimetableRequest
    ): TimetableResponse
}
