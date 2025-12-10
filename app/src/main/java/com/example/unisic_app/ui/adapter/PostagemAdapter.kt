package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Postagem

class PostagemAdapter(private val listaPostagens: List<Postagem>) :
    RecyclerView.Adapter<PostagemAdapter.PostagemViewHolder>() {

    class PostagemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val autor: TextView = itemView.findViewById(R.id.text_post_autor)
        val data: TextView = itemView.findViewById(R.id.text_post_data)
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
        holder.data.text = postagem.data
        holder.conteudo.text = postagem.texto

        // No MVP, apenas exibe. Futuramente, adicionaria a navegação para a thread completa.
    }

    override fun getItemCount(): Int = listaPostagens.size
}