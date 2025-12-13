package com.example.unisic_app.data.model

// data/model/VagaEmprego.kt
data class VagaEmprego(
    val id: String? = null,
    val titulo: String = "",
    val empresa: String = "",
    val localizacao: String = "",
    val urlInscricao: String = "", // Link para a inscrição na vaga
    val dataPublicacao: String = ""
)