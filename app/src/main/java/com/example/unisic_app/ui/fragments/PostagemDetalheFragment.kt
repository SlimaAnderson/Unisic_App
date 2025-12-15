package com.example.unisic_app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Comentario
import com.example.unisic_app.data.model.Postagem
import com.example.unisic_app.data.repository.FirebaseRepository
import com.example.unisic_app.ui.adapter.ComentarioAdapter
import com.example.unisic_app.ui.adapter.OnAutorClickListener
import com.example.unisic_app.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class PostagemDetalheFragment : Fragment(R.layout.fragment_postagem_detalhe), OnAutorClickListener {

    private val repository = FirebaseRepository()

    private lateinit var tvPostTitulo: TextView
    private lateinit var tvPostAutorData: TextView
    private lateinit var tvPostContent: TextView
    private lateinit var rvComentarios: RecyclerView
    private lateinit var etComentario: EditText
    private lateinit var btnComentar: Button

    private var currentPostId: String? = null
    private var currentPost: Postagem? = null
    private lateinit var comentarioAdapter: ComentarioAdapter

    private var postAuthorUid: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Views
        tvPostTitulo = view.findViewById(R.id.text_post_detalhe_titulo)
        tvPostAutorData = view.findViewById(R.id.text_post_detalhe_autor_data)
        tvPostContent = view.findViewById(R.id.text_post_detalhe_conteudo)

        // IDs
        rvComentarios = view.findViewById(R.id.recycler_view_comentarios)
        etComentario = view.findViewById(R.id.input_comentario)
        btnComentar = view.findViewById(R.id.button_comentar)

        // Inicializar RecyclerView e Listener
        comentarioAdapter = ComentarioAdapter(emptyList(), this)
        rvComentarios.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        rvComentarios.adapter = comentarioAdapter

        //Obter Argumentos (Verificação de segurança)
        val postId = arguments?.getString("postId")

        if (postId.isNullOrEmpty()) {
            Toast.makeText(context, "Erro: Não foi possível carregar a postagem (ID ausente).", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        currentPostId = postId

        // Carregar Conteúdo e Comentários
        carregarDetalhesEComentarios(postId)

        // Configurar Listeners

        tvPostAutorData.setOnClickListener {
            if (postAuthorUid != null && postAuthorUid!!.isNotEmpty()) {
                navigateToUserProfile(postAuthorUid!!)
            } else {
                Toast.makeText(context, "UID do autor do post não disponível.", Toast.LENGTH_SHORT).show()
            }
        }

        btnComentar.setOnClickListener {
            adicionarComentario()
        }
    }

    // ---------------------------------------------------------------------
    // LÓGICA DE NAVEGAÇÃO REUTILIZÁVEL
    // ---------------------------------------------------------------------

    // Função de Navegação Reutilizável para Perfil
    private fun navigateToUserProfile(uid: String) {
        val bundle = Bundle().apply {
            putString("profileUid", uid)
        }
        // Navega para o Fragmento de Visualização (somente leitura)
        findNavController().navigate(R.id.userViewFragment, bundle)
    }

    // Implementação da função de callback para o clique no autor do COMENTÁRIO
    override fun onAutorClicked(autorUid: String) {
        if (autorUid.isNotEmpty()) {
            // Usa a função reutilizável
            navigateToUserProfile(autorUid)
        } else {
            Toast.makeText(context, "UID do autor do comentário não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------------------------------------------------------------
    // LÓGICA DE DADOS
    // ---------------------------------------------------------------------

    private fun carregarDetalhesEComentarios(postId: String) {
        repository.getPostagemById(postId,
            onSuccess = { post ->
                if (post != null) {
                    currentPost = post

                    tvPostTitulo.text = post.titulo
                    tvPostAutorData.text = "Por: ${post.autor} | ${post.timestamp}"

                    tvPostContent.text = post.texto
                    activity?.title = post.titulo

                    comentarioAdapter.updateList(post.comentarios)

                    //CAPTURA O UID DO AUTOR DO POST (Se o Postagem data class tiver autorUid)
                    postAuthorUid = post.autorUid

                } else {
                    Toast.makeText(context, "Postagem não encontrada.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            },
            onFailure = { error ->
                Toast.makeText(context, "Erro ao carregar a postagem: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("PostDetalhe", "Erro ao carregar postagem: $postId", error)
            }
        )
    }

    private fun adicionarComentario() {
        val textoComentario = etComentario.text.toString().trim()
        val postId = currentPostId

        if (textoComentario.isEmpty()) {
            Toast.makeText(context, "Digite um comentário válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (postId == null) {
            Toast.makeText(context, "Erro interno: ID da postagem perdido.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val autor = currentUser?.email?.split("@")?.get(0) ?: "Anônimo"
        val autorUid = currentUser?.uid // Captura o UID para o modelo Comentario

        if (autorUid == null) {
            Toast.makeText(context, "Você precisa estar logado para comentar.", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val data = dateFormat.format(Date())

        val novoComentario = Comentario(
            autor = autor,
            autorUid = autorUid, // Inclui o UID no modelo
            texto = textoComentario,
            data = data
        )

        //ógica de Salvar Comentário no Repository
        repository.addComentarioToPost(postId, novoComentario,
            onSuccess = {
                Toast.makeText(context, "Comentário adicionado!", Toast.LENGTH_SHORT).show()
                etComentario.setText("")
            },
            onFailure = { error ->
                Toast.makeText(context, "Falha ao salvar o comentário: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("PostDetalhe", "Erro ao adicionar comentário: $postId", error)
            }
        )
    }
}