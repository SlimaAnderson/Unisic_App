package com.example.unisic_app.data.model

// data/model/Pergunta.kt

data class Pergunta(
    val id: String = "",
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0, // Índice da opção correta (0, 1, 2 ou 3)
    val category: String = "",
    val creatorNickname: String = "Admin",
)