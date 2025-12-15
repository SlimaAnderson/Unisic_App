package com.example.unisic_app.data.model

// Data class para representar um Comentário dentro de uma Postagem.
// O Firestore irá mapear este objeto para um Map dentro do array 'comentarios'

data class Comentario(
    val autor: String = "",        // Nick do Autor (Para exibição
    val autorUid: String = "",     //Para busca de perfil
    val texto: String = "",
    val data: String = ""
)