package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Comentario


interface OnAutorClickListener {
    fun onAutorClicked(autorUid: String)
}

class ComentarioAdapter(
    private var listaComentarios: List<Comentario>,
    // 🌟 NOVO: Recebe o listener
    private val listener: OnAutorClickListener? = null
) : RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val autor: TextView = itemView.findViewById(R.id.text_comentario_autor)
        val texto: TextView = itemView.findViewById(R.id.text_comentario_texto)
        val data: TextView = itemView.findViewById(R.id.text_comentario_data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = listaComentarios[position]

        // 1. Exibe o autor e o texto
        holder.autor.text = comentario.autor
        holder.texto.text = comentario.texto
        holder.data.text = comentario.data

        // 2. LÓGICA DE CLIQUE NO AUTOR
        holder.autor.setOnClickListener {
            // Verifica se o UID está presente e se o listener existe
            if (comentario.autorUid.isNotEmpty()) {
                listener?.onAutorClicked(comentario.autorUid)
            }
        }
    }


    override fun getItemCount(): Int = listaComentarios.size

    // Função para atualizar a lista
    fun updateList(newList: List<Comentario>) {
        listaComentarios = newList
        notifyDataSetChanged()
    }
}