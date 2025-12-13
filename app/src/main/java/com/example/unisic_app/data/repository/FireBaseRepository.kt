package com.example.unisic_app.data.repository

import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Noticia
import com.example.unisic_app.data.model.Pergunta
import com.example.unisic_app.data.model.Postagem
import com.example.unisic_app.data.model.Comentario
import com.example.unisic_app.data.model.User
import com.example.unisic_app.data.model.Profile
import com.example.unisic_app.data.model.VagaEmprego
import com.example.unisic_app.data.model.Progresso
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseRepository {

    private val db = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

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
                    // CORREÇÃO: Usar .documents.map para injetar o ID do documento
                    val posts = snapshot.documents.mapNotNull { document ->
                        val postagemBase = document.toObject(Postagem::class.java)

                        // Garante que o objeto existe e injeta o ID e o autorUid (que deve vir do DB)
                        if (postagemBase != null && document.exists()) {
                            postagemBase.copy(id = document.id)
                        } else {
                            null
                        }
                    }
                    onUpdate(posts)
                }
            }
    }

    /**
     * Adiciona uma nova postagem na coleção "comunidade" do Firestore.
     * 🌟 CORRIGIDO: Agora salva 'autor' (nick) e 'autorUid' (UID).
     */
    fun addPostagem(
        postagem: Postagem, // Postagem deve ter autor e autorUid vazios no momento da chamada
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid
        if (currentUid == null) {
            onFailure(Exception("Usuário não logado. Impossível criar post."))
            return
        }

        // 1. Obtém o Apelido (Nick)
        getUserNickByUid(currentUid,
            onSuccess = { nick ->
                // 2. Cria a postagem com o nick, UID e data
                val postagemComDados = postagem.copy(
                    autor = nick,           // Nick do usuário logado
                    autorUid = currentUid,  // 🌟 NOVO: UID do autor do post
                    data = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                )

                // 3. Salva no Firestore
                db.collection("comunidade")
                    .add(postagemComDados)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            },
            onFailure = { e ->
                onFailure(Exception("Falha ao buscar apelido: ${e.message}"))
            }
        )
    }

    /**
     * Busca uma postagem específica e monitora seus comentários em tempo real.
     */
    fun getPostagemById(
        postId: String,
        onSuccess: (Postagem?) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection("comunidade").document(postId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onFailure(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val postagem = snapshot.toObject(Postagem::class.java)

                    if (postagem != null) {
                        // INJETA O ID
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
    fun addComentarioToPost(
        postId: String,
        comentario: Comentario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("comunidade").document(postId)
            .update("comentarios", FieldValue.arrayUnion(comentario))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // ---------------------------------------------------------------
    // FUNÇÕES DE AUTENTICAÇÃO E PERFIL
    // ---------------------------------------------------------------

    /**
     * Tenta criar um novo usuário com Email e Senha.
     */
    fun registerUser(
        email: String,
        senha: String,
        nick: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Salva o apelido na coleção 'usuarios'
                        val newUser = User(
                            uid = firebaseUser.uid,
                            email = email,
                            nick = nick
                        )
                        db.collection("usuarios").document(firebaseUser.uid)
                            .set(newUser)
                            .addOnSuccessListener {
                                onSuccess() // Sucesso no Auth e no Firestore
                            }
                            .addOnFailureListener { e ->
                                onFailure("Falha ao salvar apelido: ${e.message}")
                            }
                    } else {
                        onFailure("Usuário do Firebase é nulo após o cadastro.")
                    }
                } else {
                    onFailure(task.exception?.message ?: "Erro desconhecido no cadastro.")
                }
            }
    }

    /**
     * Tenta fazer o login do usuário.
     */
    fun loginUser(
        email: String,
        senha: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Erro desconhecido no login.")
                }
            }
    }

    /**
     * Retorna o UID do usuário logado.
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Retorna o apelido (nick) do usuário logado a partir do Firestore.
     */
    fun getCurrentUserNick(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val uid = getCurrentUserId()
        if (uid != null) {
            db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    if (user?.nick != null) {
                        onSuccess(user.nick)
                    } else {
                        onFailure()
                    }
                }
                .addOnFailureListener {
                    onFailure()
                }
        } else {
            onFailure()
        }
    }

    /**
     * Busca o apelido (nick) de QUALQUER UID a partir do Firestore.
     */
    fun getUserNickByUid(
        uid: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                val nick = user?.nick ?: "Usuário Anônimo"
                onSuccess(nick)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Faz o logout do usuário.
     */
    fun logoutUser() {
        auth.signOut()
    }

    /**
     * Busca os dados completos do perfil (Bio, nome, empresa) de QUALQUER UID.
     */
    fun getUserProfile(
        uid: String,
        onSuccess: (Profile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                // Mapeia o documento para a classe Profile
                val profile = Profile(
                    uid = uid,
                    name = document.getString("name") ?: "",
                    company = document.getString("company") ?: "",
                    description = document.getString("description") ?: ""
                )
                onSuccess(profile)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Atualiza (ou cria) os dados da Bio do usuário logado.
     */
    fun updateProfile(
        uid: String,
        name: String,
        company: String,
        description: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val data = mapOf(
            "name" to name,
            "company" to company,
            "description" to description
        )

        db.collection("usuarios").document(uid)
            .update(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                // Se o documento não existir, tenta criar com merge.
                if (e.message?.contains("NOT_FOUND") == true) {
                    db.collection("usuarios").document(uid)
                        .set(data, com.google.firebase.firestore.SetOptions.merge())
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e2 -> onFailure(e2) }
                } else {
                    onFailure(e)
                }
            }
    }


    // =======================================================================
// III. FUNÇÕES DA TELA HOME (NOTÍCIAS E VAGAS - FIRESTORE)
// =======================================================================

    /**
     * Configura um listener em tempo real para a coleção "noticias",
     * ordenando pela data mais recente.
     */
    fun getNoticiasRealtime(onUpdate: (List<Noticia>) -> Unit): ListenerRegistration {
        return db.collection("noticias")
            // Ordena por data (assumindo que "data" é um Timestamp ou String consistente)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Erro ao ouvir notícias: $e")
                    // Se houver falha, pode retornar uma lista vazia ou logs de erro
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Mapeia os documentos para a data class Noticia
                    val noticias = snapshot.documents.mapNotNull { document ->
                        // O ID do documento é injetado no modelo
                        document.toObject(Noticia::class.java)?.copy(id = document.id)
                    }
                    onUpdate(noticias)
                }
            }

    }

    /**
     * Busca a lista de vagas de emprego UMA VEZ (não em tempo real).
     */
    fun getVagasEmpregoOnce(onSuccess: (List<VagaEmprego>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("vagas")
            .orderBy("dataPublicacao", Query.Direction.DESCENDING)
            .get() // Usa get() em vez de addSnapshotListener
            .addOnSuccessListener { snapshot ->
                val vagas = snapshot.documents.mapNotNull { document ->
                    // Assumindo que você tem a data class VagaEmprego
                    document.toObject(VagaEmprego::class.java)?.copy(id = document.id)
                }
                onSuccess(vagas)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Configura um listener em tempo real para a coleção "vagas",
     * ordenando pela data de publicação mais recente.
     */
    fun getVagasEmpregoRealtime(onUpdate: (List<VagaEmprego>) -> Unit): ListenerRegistration {
        return db.collection("vagas")
            // Ordena pela data de publicação
            .orderBy("dataPublicacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Erro ao ouvir vagas: $e")
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Mapeia os documentos para a data class VagaEmprego
                    val vagas = snapshot.documents.mapNotNull { document ->
                        document.toObject(VagaEmprego::class.java)?.copy(id = document.id)
                    }
                    onUpdate(vagas)
                }
            }
    }

    fun getNoticiasOnce(onSuccess: (List<Noticia>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("noticias")
            .orderBy("data", Query.Direction.DESCENDING)
            .get() // Usa get() em vez de addSnapshotListener
            .addOnSuccessListener { snapshot ->
                val noticias = snapshot.documents.mapNotNull { document ->
                    document.toObject(Noticia::class.java)?.copy(id = document.id)
                }
                onSuccess(noticias)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

// =======================================================================
// IV. FUNÇÕES DO QUIZ (FIREBASE FIRESTORE - Coleção 'perguntas')
// =======================================================================

    /**
     * Busca todas as perguntas do Quiz uma única vez para iniciar uma sessão.
     */
    fun getQuizQuestionsOnce(
        onSuccess: (List<Pergunta>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("perguntas") // Coleção confirmada
            .get()
            .addOnSuccessListener { snapshot ->
                val perguntas = snapshot.documents.mapNotNull { document ->
                    // Mapeia os documentos para a data class Pergunta
                    document.toObject(Pergunta::class.java)?.copy(id = document.id)
                }
                onSuccess(perguntas)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Salva uma nova pergunta criada por um usuário na coleção 'perguntas'.
     */
    fun submitUserQuestion(
        question: Pergunta,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("perguntas")
            .add(question)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    /**
     * Retorna a lista estática de módulos de curso.
     */
    fun getModulosCurso(): List<ModuloCurso> {
        // Dados de exemplo para a lista de Módulos
        return listOf(
            ModuloCurso(1, "Introdução", "Visão geral da segurança", "A segurança digital é um estado de espírito... Conteúdo completo aqui."),
            ModuloCurso(2, "Iniciante", "Senhas e 2FA", "Aprenda a criar senhas fortes e a importância da Autenticação de Dois Fatores.")
        )
    }

    /**
     * Busca um módulo de curso estático por ID.
     */
    fun getModuloCurso(id: Int): ModuloCurso? {
        return getModulosCurso().find { it.id == id }
    }

// =======================================================================
// V. FUNÇÕES DE PROGRESSO (CURSOS) 🌟 ADICIONADAS
// =======================================================================

    /**
     * Caminho: users/{userId}/progresso/{moduleId}
     * Salva o estado atual de progresso de um módulo (última seção vista).
     */
    fun saveCourseProgress(
        userId: String,
        progress: Progresso,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (userId.isEmpty() || progress.moduleId.isEmpty()) {
            onFailure(Exception("UserID ou ModuleID inválido."))
            return
        }

        db.collection("users").document(userId)
            .collection("progresso").document(progress.moduleId)
            // Usamos set(progress) para criar ou atualizar o documento
            .set(progress)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener(onFailure)
    }

    /**
     * Recupera todos os registros de progresso para um usuário.
     */
    fun getProgressForUser(
        userId: String,
        onSuccess: (List<Progresso>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (userId.isEmpty()) {
            onFailure(Exception("UserID inválido."))
            return
        }

        db.collection("users").document(userId)
            .collection("progresso")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val progressList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Progresso::class.java)
                }
                onSuccess(progressList)
            }
            .addOnFailureListener(onFailure)
    }

    fun getCourseProgress(
        userId: String,
        moduleId: String,
        onSuccess: (Progresso?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // 1. Obter a referência ao documento de progresso
        // (A estrutura pode variar, mas assumindo uma coleção 'progressos' dentro do documento do usuário)
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios")
            .document(userId)
            .collection("progressos")
            .document(moduleId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val progresso = documentSnapshot.toObject(Progresso::class.java)
                onSuccess(progresso)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}