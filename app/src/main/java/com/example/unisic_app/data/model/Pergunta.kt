package com.example.unisic_app.data.model

data class Pergunta(

    val id: String? = null,  //valor null para que o Firestore gere ID
    val questionText: String = "",
    val options: List<String> = emptyList(),

    val correctAnswerIndex: String? = null,

    val category: String = "",
    val creatorNickname: String = "Admin",
)