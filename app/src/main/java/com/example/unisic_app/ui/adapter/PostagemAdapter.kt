package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Postagem
import android.os.Bundle
import androidx.core.os.bundleOf

class PostagemAdapter(
    private var listaPostagens: List<Postagem>,
    private val navController: NavController // 🌟 Este é o parâmetro que o Fragment agora deve fornecer
) : RecyclerView.Adapter<PostagemAdapter.PostagemViewHolder>() {
    class PostagemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_post_titulo) // 🌟 NOVO CAMPO
        val autor: TextView = itemView.findViewById(R.id.text_post_autor)
        val conteudo: TextView = itemView.findViewById(R.id.text_post_conteudo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostagemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_postagem, parent, false)
        return PostagemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostagemViewHolder, position: Int) {
        val postagem = listaPostagens[position]

        holder.autor.text = postagem.autor
        holder.conteudo.text = postagem.texto
        holder.titulo.text = postagem.titulo

        holder.autor.text = "Por: ${postagem.autor} | ${postagem.data}"

        holder.conteudo.text = postagem.texto

        // Lógica de clique para NAVEGAR
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                // 🌟 Passa o ID do documento Firestore (que será o ID do Post)
                putString("postId", postagem.id)
            }

            navController.navigate(
                R.id.action_comunidadeFragment_to_postagemDetalheFragment, // 🌟 NOVA AÇÃO
                bundle
            )
        }
    }

    // 🌟 NOVO MÉTODO: Para atualizar a lista a partir do LiveData
    fun updateList(newList: List<Postagem>) {
        listaPostagens = newList
        notifyDataSetChanged() // Notifica o RecyclerView sobre a mudança
    }

    override fun getItemCount(): Int = listaPostagens.size
}