package com.example.unisic_app.data.repository

import android.util.Log
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
import com.google.firebase.firestore.SetOptions
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
     * priorizando postagens fixadas.
     */
    fun getPostsRealtime(onUpdate: (List<Postagem>) -> Unit): ListenerRegistration {
        return db.collection("comunidade")
            .orderBy("pinned", Query.Direction.DESCENDING)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // O link para criar o índice composto no console aparece aqui.
                    Log.e("FirebaseRepo", "Erro ao ouvir posts (Verifique o Índice Composto!): ${e.message}", e)
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { document ->
                        val postagemBase = document.toObject(Postagem::class.java)

                        if (postagemBase != null && document.exists()) {
                            // Injeta o ID do documento (String) no modelo.
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
     */
    fun addPostagem(
        postagem: Postagem,
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
                // 2. Cria a postagem com o nick, UID e data/timestamp
                val postagemComDados = postagem.copy(
                    autor = nick,
                    autorUid = currentUid,
                    timestamp = System.currentTimeMillis()
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
                        // INJETA O ID do documento
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
                        .set(data, SetOptions.merge())
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
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FirebaseRepo", "Erro ao ouvir notícias: $e")
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val noticias = snapshot.documents.mapNotNull { document ->
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
            .get()
            .addOnSuccessListener { snapshot ->
                val vagas = snapshot.documents.mapNotNull { document ->
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
            .orderBy("dataPublicacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FirebaseRepo", "Erro ao ouvir vagas: $e")
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
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
            .get()
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
// IV. FUNÇÕES DO QUIZ E CURSOS (FIREBASE FIRESTORE)
// =======================================================================

    /**
     * Busca todas as perguntas do Quiz uma única vez para iniciar uma sessão.
     * CORREÇÃO: Usa o ID do documento (String) para injetar no modelo.
     */
    fun getQuizQuestionsOnce(
        onSuccess: (List<Pergunta>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("perguntas")
            .get()
            .addOnSuccessListener { snapshot ->
                val perguntas = snapshot.documents.mapNotNull { document ->
                    val pergunta = document.toObject(Pergunta::class.java)


                    pergunta?.copy(id = document.id)
                }
                onSuccess(perguntas.filterNotNull())
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
     * 💡 Configura um listener em tempo real para carregar a lista de Módulos de Curso.
     * CORREÇÃO: Injeta o ID do documento (String) no ModuloCurso.id.
     */
    fun getModulosRealtime(onUpdate: (List<ModuloCurso>) -> Unit): ListenerRegistration {
        return db.collection("modulos_curso")
            // Ordena os módulos pelo campo 'order'
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FirebaseRepo", "Erro ao ouvir módulos: ${e.message}", e)
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val modulos = snapshot?.documents?.mapNotNull { document ->
                    try {
                        val moduloBase = document.toObject(ModuloCurso::class.java)


                        val firestoreIdLong = document.getLong("id")
                        val idParaModelo = firestoreIdLong?.toString() ?: document.id

                        moduloBase?.copy(id = idParaModelo)

                    } catch (e: Exception) {
                        Log.e("FirebaseRepo", "Erro de desserialização em ModuloCurso: ${e.message}", e)
                        null
                    }
                } ?: emptyList()

                onUpdate(modulos.filterNotNull())
            }
    }

    /**
     * Busca um módulo de curso específico pelo ID do documento (para a tela de detalhe).
     * CORREÇÃO: Força a leitura do campo 'id' (que é Long no Firestore) para String no modelo.
     */
    fun getModuloCursoById(moduleId: String, onSuccess: (ModuloCurso?) -> Unit, onFailure: (Exception) -> Unit) {
        // ASSUME que o moduleId é o ID do DOCUMENTO (String) que foi passado pela navegação.
        db.collection("modulos_curso").document(moduleId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val modulo = try {
                        val moduloBase = document.toObject(ModuloCurso::class.java)


                        val firestoreIdLong = document.getLong("id")
                        val idParaModelo = firestoreIdLong?.toString() ?: document.id

                        moduloBase?.copy(id = idParaModelo)

                    } catch (e: Exception) {
                        Log.e("FirebaseRepo", "Erro de desserialização: ${e.message}", e)
                        null
                    }
                    onSuccess(modulo)
                } else {
                    onSuccess(null) // Documento não encontrado
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
// =======================================================================
// V. FUNÇÕES DE PROGRESSO (CURSOS)
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

        // Caminho corrigido para consistência
        db.collection("usuarios").document(userId)
            .collection("progresso").document(progress.moduleId)
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

        db.collection("usuarios").document(userId)
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

        db.collection("usuarios")
            .document(userId)
            .collection("progresso")
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