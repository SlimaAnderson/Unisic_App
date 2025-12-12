package com.example.unisic_app.data.model

// Modelo para posts do Fórum/Comunidade
data class Postagem(
    // Adicionar valores padrão é CRUCIAL para o Firebase e para corrigir o erro de construtor
    val id: String? = null,
    val autor: String = "",
    val titulo: String = "",
    val texto: String = "",
    val data: String = "",

    val comentarios: List<Comentario> = emptyList()
)