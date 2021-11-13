package com.findme.app.model

data class Sensor(
    val locations: List<Location>? = emptyList(),
    val name: String? = "",
    val timestamp: String? = ""
)