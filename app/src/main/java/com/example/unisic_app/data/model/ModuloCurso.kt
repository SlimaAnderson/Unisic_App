package com.example.unisic_app.data.model

import com.google.firebase.firestore.PropertyName

data class ModuloCurso(
    // CORREÇÃO: O Firestore salva "Number" que o Kotlin lê como Long.
    // Mudar para Long? garante o mapeamento direto.
    val id: String? = null,

    // Campo para exibição na lista de módulos (o TÍTULO principal)
    val titulo: String = "",

    // Campo de descrição curta, exibido na lista (pode ser usado como descrição)
    val descricao: String = "",

    // 💡 NOVO: Subtítulo/Resumo para a Tela de Detalhe
    val subtitulo: String = "",

    // 💡 NOVO: O Conteúdo longo/detalhado para a Tela de Detalhe
    val conteudo: String = "",

    val videoUrl: String? = null,
    val order: Int = 0 // Manter Int, pois o 'order' não é argumento de navegação
)