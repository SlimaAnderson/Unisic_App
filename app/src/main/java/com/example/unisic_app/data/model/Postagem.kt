package com.example.unisic_app.data.model

// Modelo para posts do Fórum/Comunidade
data class Postagem(
    // Adicionar valores padrão é CRUCIAL para o Firebase
    val id: String? = null,
    val autor: String = "",        // Nick do Autor

    // 🌟 CORREÇÃO CRÍTICA 🌟
    // O UID é necessário para navegar para o perfil do autor do post
    val autorUid: String? = null,

    val titulo: String = "",
    val texto: String = "",
    val data: String = "",

    val comentarios: List<Comentario> = emptyList()
)