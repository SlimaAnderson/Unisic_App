package com.example.unisic_app.data.model

data class Profile(
    val uid: String = "",
    val name: String = "",         // Nome Completo
    val company: String = "",      // Local de trabalho/Empresa
    val description: String = ""   // Pequena descrição/Bio
)