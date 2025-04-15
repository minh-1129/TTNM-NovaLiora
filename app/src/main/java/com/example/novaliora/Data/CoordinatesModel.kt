package com.example.novaliora.Data

data class CoordinatesModel(
    val result: List<Result>?,
    val error: String?,

    val response: String?,

    val labels: List<String>?,
    val polygons: List<List<List<Double>>>?
)