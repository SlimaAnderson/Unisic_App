package com.example.unisic_app.data.model

// Data class para representar um Comentário dentro de uma Postagem.
// O Firestore irá mapear este objeto para um Map dentro do array 'comentarios'
// do documento principal do Post.
// data/model/Comentario.kt
data class Comentario(
    val autor: String = "",        // Nick do Autor (Para exibição)
    val autorUid: String = "",     // 🌟 NOVO: UID do autor (Para busca de perfil)
    val texto: String = "",
    val data: String = ""
)