package com.example.unisic_app.data.model

// data/model/Pergunta.kt

data class Pergunta(
    // Adicionar valores padrão é crucial para o Firebase
    val id: String? = null,              // Usando String? e valor null para que o Firestore gere ID
    val questionText: String = "",
    val options: List<String> = emptyList(),

    // 🛑 CORREÇÃO CRÍTICA: Mudar para String? para ler o dado do Firebase
    // que foi salvo incorretamente como String em vez de Number.
    // A lógica de Quiz terá que converter isso para Int antes de usar.
    val correctAnswerIndex: String? = null,

    val category: String = "",
    val creatorNickname: String = "Admin",
)