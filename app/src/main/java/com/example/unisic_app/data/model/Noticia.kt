package com.example.unisic_app.data.model

// data/model/Noticia.kt
data class Noticia(
    val id: String? = null,
    val titulo: String = "",
    val url: String = "", // Link para a notícia completa
    val data: String = ""
)