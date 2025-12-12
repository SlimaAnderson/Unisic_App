package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Comentario

class ComentarioAdapter(private var listaComentarios: List<Comentario>) :
    RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val autor: TextView = itemView.findViewById(R.id.text_comentario_autor)
        val texto: TextView = itemView.findViewById(R.id.text_comentario_texto)
        val data: TextView = itemView.findViewById(R.id.text_comentario_data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false) // Criar este layout
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = listaComentarios[position]
        holder.autor.text = comentario.autor
        holder.texto.text = comentario.texto
        holder.data.text = comentario.data
    }

    override fun getItemCount(): Int = listaComentarios.size

    fun updateList(newList: List<Comentario>) {
        listaComentarios = newList
        notifyDataSetChanged()
    }
}