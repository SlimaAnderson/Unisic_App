package com.example.unisic_app.data.model

import com.google.firebase.firestore.PropertyName

data class ModuloCurso(

    val id: String? = null,

    // Campo para exibição na lista de módulos (o TÍTULO principal)
    val titulo: String = "",

    // Campo de descrição curta, exibido na lista (pode ser usado como descrição)
    val descricao: String = "",

    // 💡 Resumo para a Tela de Detalhe
    val subtitulo: String = "",

    val conteudo: String = "",

    val videoUrl: String? = null,
    val order: Int = 0
)