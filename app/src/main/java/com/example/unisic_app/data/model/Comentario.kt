package com.example.unisic_app.data.model

// Data class para representar um Comentário dentro de uma Postagem.
// O Firestore irá mapear este objeto para um Map dentro do array 'comentarios'
// do documento principal do Post.
data class Comentario(
    val autor: String = "",
    val texto: String = "",
    val data: String = ""
)