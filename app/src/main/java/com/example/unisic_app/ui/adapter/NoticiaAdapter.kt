// ui/adapter/NoticiaAdapter.kt
package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Noticia

class NoticiaAdapter(
    private var listaNoticias: List<Noticia>,
    private val clickListener: (Noticia) -> Unit
) : RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder>() {

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_noticia_titulo)
        val data: TextView = itemView.findViewById(R.id.text_noticia_data)

        fun bind(noticia: Noticia, clickListener: (Noticia) -> Unit) {
            titulo.text = noticia.titulo
            data.text = noticia.data
            itemView.setOnClickListener {
                clickListener(noticia)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        holder.bind(listaNoticias[position], clickListener)
    }

    override fun getItemCount(): Int = listaNoticias.size

    fun updateList(newList: List<Noticia>) {
        listaNoticias = newList
        notifyDataSetChanged()
    }
}