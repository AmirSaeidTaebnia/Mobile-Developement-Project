package com.example.thetaskmanagerapp

import com.google.gson.annotations.SerializedName

data class TimetableResponse(
    val reservations: List<Reservation>
)

data class Reservation(
    val id: String,
    val subject: String?,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val resources: List<Resource>?
)

data class Resource(
    val id: String,
    val type: String,
    val code: String?,
    val name: String?
)

data class TimetableRequest(
    val rangeStart: String,
    val rangeEnd: String,
    val studentGroup: List<String>
)
