package com.example.unisic_app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Progresso

class CursosAdapter(
    private val listaModulos: List<ModuloCurso>,
    private var progressoUsuario: List<Progresso>, // Deve ser 'var' para ser atualizado
    private val navController: NavController
) : RecyclerView.Adapter<CursosAdapter.CursoViewHolder>() {

    class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_modulo_titulo)
        val descricao: TextView = itemView.findViewById(R.id.text_modulo_descricao)
        val statusIcon: ImageView = itemView.findViewById(R.id.image_modulo_status_icon)
    }

    // MÉTODO ESSENCIAL PARA ATUALIZAÇÃO NO onResume
    fun updateProgressList(newProgressoList: List<Progresso>) {
        progressoUsuario = newProgressoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_modulo_curso, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val context = holder.itemView.context
        val modulo = listaModulos[position]

        val progresso = progressoUsuario.find { it.moduleId == modulo.id.toString() }

        // 🌟 CORREÇÃO: Captura o status de conclusão para ser passado como argumento
        val isCompletedStatus = progresso?.completed ?: false

        holder.titulo.text = modulo.titulo
        holder.descricao.text = modulo.descricao

        // Lógica de Status (Ícone e Cor do Título)
        if (progresso != null) {
            if (progresso.completed) {
                // Status: CONCLUÍDO (Ícone VERDE)
                holder.statusIcon.setImageResource(R.drawable.circle_status_concluido)
                holder.titulo.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess))
            } else {
                // Status: EM PROGRESSO (Ícone AMARELO)
                holder.statusIcon.setImageResource(R.drawable.circle_status_progresso)
                holder.titulo.setTextColor(ContextCompat.getColor(context, R.color.colorProgressYellow))
            }
        } else {
            // Status: NÃO INICIADO (Ícone BRANCO/Borda Preta)
            holder.statusIcon.setImageResource(R.drawable.circle_status_nao_iniciado)
            holder.titulo.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        // Lógica de clique para NAVEGAR
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("moduloId", modulo.id)
                putString("moduloTitulo", modulo.titulo)
                // 🌟 CORREÇÃO FINAL AQUI: Passando o status de conclusão para o Detalhe
                putBoolean("isAlreadyCompleted", isCompletedStatus)
            }
            navController.navigate(
                R.id.action_cursosFragment_to_cursoDetalheFragment,
                bundle
            )
        }
    }

    override fun getItemCount(): Int = listaModulos.size

}