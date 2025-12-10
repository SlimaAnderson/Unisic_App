package com.example.unisic_app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.ModuloCurso

// O Adapter agora precisa receber o NavController para realizar a navegação
class CursosAdapter(
    private val listaModulos: List<ModuloCurso>,
    private val navController: NavController
) : RecyclerView.Adapter<CursosAdapter.CursoViewHolder>() {

    class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_modulo_titulo)
        val descricao: TextView = itemView.findViewById(R.id.text_modulo_descricao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_modulo_curso, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val modulo = listaModulos[position]

        holder.titulo.text = modulo.titulo
        holder.descricao.text = modulo.descricao

        // Lógica de clique para NAVEGAR
        holder.itemView.setOnClickListener {
            // Cria um Bundle para passar os argumentos (ID e Título)
            val bundle = Bundle().apply {
                putInt("moduloId", modulo.id)
                putString("moduloTitulo", modulo.titulo)
            }

            // Navega usando a AÇÃO definida no nav_graph.xml
            navController.navigate(
                R.id.action_cursosFragment_to_cursoDetalheFragment,
                bundle
            )
        }
    }

    override fun getItemCount(): Int = listaModulos.size
}