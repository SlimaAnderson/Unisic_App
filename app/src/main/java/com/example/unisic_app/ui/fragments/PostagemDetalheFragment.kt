package com.example.unisic_app.ui.fragments

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
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputEditText // Mantido por segurança, caso o R.id seja um TextInput
import java.text.SimpleDateFormat
import java.util.*

class PostagemDetalheFragment : Fragment(R.layout.fragment_postagem_detalhe) {

    private val repository = FirebaseRepository()
    private lateinit var tvPostContent: TextView
    private lateinit var rvComentarios: RecyclerView
    private lateinit var etComentario: EditText
    private lateinit var btnComentar: Button

    private var currentPostId: String? = null
    private var currentPost: Postagem? = null
    private lateinit var comentarioAdapter: ComentarioAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar Views
        tvPostContent = view.findViewById(R.id.text_post_detalhe_conteudo)
        rvComentarios = view.findViewById(R.id.recycler_view_comentarios)

        // CORRIGIDO: Usando a referência genérica EditText (ou TextInputEditText se for o caso)
        // Se o seu layout usa TextInputEditText, o findViewById o encontra.
        etComentario = view.findViewById(R.id.input_comentario)

        btnComentar = view.findViewById(R.id.button_comentar)

        // Inicializar RecyclerView de Comentários
        // Assumindo que ComentarioAdapter está na pasta correta e possui o método updateList
        comentarioAdapter = ComentarioAdapter(emptyList())
        rvComentarios.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        rvComentarios.adapter = comentarioAdapter

        // 2. Obter Argumentos (Verificação de segurança contra crash por ID ausente)
        val postId = arguments?.getString("postId")

        if (postId.isNullOrEmpty()) {
            Toast.makeText(context, "Erro: Não foi possível carregar a postagem (ID ausente).", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        currentPostId = postId

        // 3. Carregar Conteúdo e Comentários
        carregarDetalhesEComentarios(postId)

        // 4. Configurar o Botão de Comentário
        btnComentar.setOnClickListener {
            adicionarComentario()
        }
    }

    private fun carregarDetalhesEComentarios(postId: String) {
        // Busca a postagem no Repositório. O ListenerRegistration permite atualizações em tempo real.
        repository.getPostagemById(postId,
            onSuccess = { post ->
                if (post != null) {
                    currentPost = post

                    // Acessando as propriedades 'texto' e 'titulo' do data class Postagem
                    tvPostContent.text = post.texto
                    activity?.title = post.titulo

                    comentarioAdapter.updateList(post.comentarios)
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

        // Obtém o nome de usuário ou "Anônimo"
        val currentUser = FirebaseAuth.getInstance().currentUser
        val autor = currentUser?.email?.split("@")?.get(0) ?: "Anônimo"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val data = dateFormat.format(Date())

        val novoComentario = Comentario(
            autor = autor,
            texto = textoComentario,
            data = data
        )

        // 5. Lógica de Salvar Comentário no Repository
        repository.addComentarioToPost(postId, novoComentario,
            onSuccess = {
                Toast.makeText(context, "Comentário adicionado!", Toast.LENGTH_SHORT).show()
                etComentario.setText("") // Limpa o campo de entrada
            },
            onFailure = { error ->
                Toast.makeText(context, "Falha ao salvar o comentário: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("PostDetalhe", "Erro ao adicionar comentário: $postId", error)
            }
        )
    }
}
// ❌ CERTIFIQUE-SE DE QUE O BLOCO MOCK FOI REMOVIDO DAQUI