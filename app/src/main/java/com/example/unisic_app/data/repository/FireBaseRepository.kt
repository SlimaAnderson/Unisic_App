package com.example.unisic_app.data.repository

import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Noticia
import com.example.unisic_app.data.model.Pergunta
import com.example.unisic_app.data.model.Postagem
import com.example.unisic_app.data.model.Comentario
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseRepository {

    private val db = Firebase.firestore

    // =======================================================================
    // I. FUNÇÕES DO FÓRUM (INTERATIVO - FIREBASE FIRESTORE)
    // =======================================================================

    /**
     * Configura um listener em tempo real para a coleção "comunidade",
     * INJETANDO O ID DO DOCUMENTO na classe Postagem.
     */
    fun getPostsRealtime(onUpdate: (List<Postagem>) -> Unit): ListenerRegistration {
        return db.collection("comunidade")
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Erro ao ouvir posts: $e")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // 🌟 CORREÇÃO: Usar .documents.map para injetar o ID do documento
                    val posts = snapshot.documents.mapNotNull { document ->
                        val postagemBase = document.toObject(Postagem::class.java)

                        // Garante que o objeto existe e injeta o ID
                        if (postagemBase != null && document.exists()) {
                            // Assumindo que Postagem é um data class e tem val id: String?
                            postagemBase.copy(id = document.id)
                        } else {
                            null // Ignora documentos que não puderam ser mapeados (corrompidos)
                        }
                    }
                    onUpdate(posts)
                }
            }
    }

    /**
     * Adiciona uma nova postagem na coleção "comunidade" do Firestore.
     */
    fun addPostagem(postagem: Postagem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val postagemComData = postagem.copy(
            data = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        )

        db.collection("comunidade")
            .add(postagemComData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Busca uma postagem específica e monitora seus comentários em tempo real.
     * INJETA O ID DO DOCUMENTO.
     */
    fun getPostagemById(postId: String, onSuccess: (Postagem?) -> Unit, onFailure: (Exception) -> Unit): ListenerRegistration {
        // A lógica de validação de postId (null/empty) é tratada no Fragmento.
        return db.collection("comunidade").document(postId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onFailure(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    // 🌟 CORREÇÃO: Pega o objeto base
                    val postagem = snapshot.toObject(Postagem::class.java)

                    if (postagem != null) {
                        // 🌟 INJETA O ID (mesmo que o ID esteja correto no DocumentReference,
                        // é bom injetá-lo na classe para consistência.)
                        onSuccess(postagem.copy(id = snapshot.id))
                    } else {
                        onSuccess(null)
                    }
                } else {
                    onSuccess(null)
                }
            }
    }

    /**
     * Adiciona um novo comentário à lista de comentários de uma postagem.
     */
    fun addComentarioToPost(postId: String, comentario: Comentario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("comunidade").document(postId)
            .update("comentarios", FieldValue.arrayUnion(comentario))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // =======================================================================
    // II. DADOS ESTÁTICOS (MANUTENÇÃO)
    // =======================================================================
    // ... (Métodos de dados estáticos omitidos por brevidade)

    fun getPostagensForum(): List<Postagem> {
        return listOf(
            Postagem(autor = "Admin", texto = "Bem-vindos à comunidade UNISIC!", data = "01/12/2025"),
            Postagem(autor = "Maria S.", texto = "Alguém tem uma boa recomendação de Gerenciador de Senhas gratuito?", data = "03/12/2025")
        )
    }

    fun getNoticias(): List<Noticia> { /* ... */ return emptyList() }
    fun getPerguntas(): List<Pergunta> { /* ... */ return emptyList() }
    fun getModulosCurso(): List<ModuloCurso> { /* ... */ return emptyList() }
    fun getModuloCurso(id: Int): ModuloCurso? { return getModulosCurso().find { it.id == id } }
}