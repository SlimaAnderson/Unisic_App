package com.example.unisic_app.data.model

// D:/App Unisic/app/src/main/java/com/example/unisic_app/data/model/Progresso.kt

data class Progresso(
    val moduleId: String = "",
    val lastSection: String? = null,
    val completed: Boolean = false, // 🌟 CORREÇÃO: Mude isCompleted para completed
    val timestamp: Long = System.currentTimeMillis()
)