package com.example.unisic_app.data.model

import com.google.firebase.firestore.PropertyName

// Modelo para posts do Fórum/Comunidade
data class Postagem(
    // Adicionar valores padrão
    val id: String? = null,
    val autor: String = "", // Nick do Autor


    @get:PropertyName("pinned") // Usado para leitura (getter)
    @set:PropertyName("pinned") // Usado para escrita (setter)
    var isPinned: Boolean = false,

    //O UID é necessário para navegar para o perfil do autor do post
    val autorUid: String? = null,

    val titulo: String = "",
    val texto: String = "",

    val timestamp: Long = 0L,

    val comentarios: List<Comentario> = emptyList()
)