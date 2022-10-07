package com.example.bustravo

data class Driver(
    val name: String,
    val email: String,
    val password: String,
    val adhaar: String,
    val numberPlate: String,
    val longitude: Double?,
    val latitude: Double?
)
