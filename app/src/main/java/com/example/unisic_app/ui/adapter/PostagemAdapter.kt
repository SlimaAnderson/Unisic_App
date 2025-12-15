package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Postagem
import android.os.Bundle

class PostagemAdapter(
    private var listaPostagens: List<Postagem>,
    private val navController: NavController
) : RecyclerView.Adapter<PostagemAdapter.PostagemViewHolder>() {

    class PostagemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_post_titulo)
        val autor: TextView = itemView.findViewById(R.id.text_post_autor)
        val conteudo: TextView = itemView.findViewById(R.id.text_post_conteudo)
        val iconPinned: ImageView = itemView.findViewById(R.id.icon_post_pinned)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostagemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_postagem, parent, false)
        return PostagemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostagemViewHolder, position: Int) {
        val postagem = listaPostagens[position]


        // Validação da data
        val dataFormatada = if (postagem.timestamp > 0) {
            // Se o timestamp for válido, formata
            val date = java.util.Date(postagem.timestamp)
            java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(date)
        } else {
            "Data Indisponível"
        }

        // Atribuição de Conteúdo
        holder.autor.text = "Por: ${postagem.autor} | ${dataFormatada}"
        holder.conteudo.text = postagem.texto
        holder.titulo.text = postagem.titulo

        // Lógica para exibir o ícone 'fixado'
        if (postagem.isPinned) {
            holder.iconPinned.visibility = View.VISIBLE
            // Altere a cor de fundo ou título para destacar
        } else {
            holder.iconPinned.visibility = View.GONE
        }


        // Lógica de clique para NAVEGAR
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                // Passa o ID do documento Firestore (que será o ID do Post)
                putString("postId", postagem.id)
            }

            navController.navigate(
                R.id.action_comunidadeFragment_to_postagemDetalheFragment,
                bundle
            )
        }
    }

    // Método para atualizar a lista a partir do LiveData
    fun updateList(newList: List<Postagem>) {
        listaPostagens = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listaPostagens.size
}