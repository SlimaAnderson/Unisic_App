package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Noticia

class NoticiaAdapter(private val listaNoticias: List<Noticia>) :
    RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder>() {

    // 1. Cria a referência para os Views do item de layout
    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_noticia_titulo)
        // val icon: ImageView = itemView.findViewById(R.id.icon_noticia) // Se quiser usar o ícone
    }

    // 2. Cria os ViewHolders (infla o layout XML)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    // 3. Conecta os dados aos Views
    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = listaNoticias[position]
        holder.titulo.text = noticia.titulo

        // Lógica de clique para abrir o link (futuramente)
        holder.itemView.setOnClickListener {
            // Exemplo: Navegar para o navegador com noticia.link
        }
    }

    // 4. Retorna a contagem de itens
    override fun getItemCount(): Int = listaNoticias.size
}