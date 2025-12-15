package com.example.unisic_app.data.model

data class Progresso(
    val moduleId: String = "",
    val lastSection: String? = null,
    val completed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)