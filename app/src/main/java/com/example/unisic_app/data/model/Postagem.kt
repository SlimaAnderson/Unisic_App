package com.example.unisic_app.data.model

import com.google.firebase.firestore.PropertyName // 🌟 Adicionar este import

// Modelo para posts do Fórum/Comunidade
data class Postagem(
    // Adicionar valores padrão é CRUCIAL para o Firebase
    val id: String? = null,
    val autor: String = "",        // Nick do Autor

    // 🌟 CORREÇÃO DE MAPEAMENTO: Mapeia a propriedade 'isPinned' do Kotlin
    // para o nome do campo 'pinned' no Firestore (conforme visto no DB).
    @get:PropertyName("pinned") // Usado para leitura (getter)
    @set:PropertyName("pinned") // Usado para escrita (setter)
    var isPinned: Boolean = false,

    // 🌟 CORREÇÃO CRÍTICA: O UID é necessário para navegar para o perfil do autor do post
    val autorUid: String? = null,

    val titulo: String = "",
    val texto: String = "",

    // 💡 MELHORIA: Usamos Long (Timestamp) para ordenação correta no Firestore
    val timestamp: Long = 0L, // Valor padrão 0 para que System.currentTimeMillis() seja definido no Repositório

    val comentarios: List<Comentario> = emptyList()
)
// Nota: Certifique-se de que sua classe Comentario (se for usada) também está definida corretamente.