package com.example.unisic_app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Progresso

class CursosAdapter(
    private var modulosList: List<ModuloCurso>,
    private var progressoUsuario: List<Progresso>,
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
        val moduloCurso = modulosList[position]

        // 💡 LENDO ID COMO STRING (DO MODELO CORRIGIDO)
        val moduleIdString = moduloCurso.id

        // 1. Busca o status de progresso
        val progresso = progressoUsuario.find { it.moduleId == moduleIdString }

        // 2. Define a variável de status
        val isCompletedStatus = progresso?.completed ?: false

        holder.titulo.text = moduloCurso.titulo
        holder.descricao.text = moduloCurso.descricao

        // Lógica de Status (Ícone e Cor do Título)
        if (progresso != null) {
            if (isCompletedStatus) {
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

            // Verifica se o ID é válido antes de navegar
            if (moduleIdString.isNullOrEmpty()) {
                Toast.makeText(context, "Erro: ID do módulo inválido para navegação.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                // ✅ CORREÇÃO CRÍTICA: ENVIANDO O ID COMO STRING (putString)
                putString("moduloId", moduleIdString)

                // Passa o status de conclusão.
                putBoolean("isAlreadyCompleted", isCompletedStatus)
            }

            // Navega para a próxima tela
            navController.navigate(R.id.cursoDetalheFragment, bundle)
        }
    }

    override fun getItemCount(): Int = modulosList.size

    fun updateModulosList(newModulos: List<ModuloCurso>) {
        this.modulosList = newModulos
        notifyDataSetChanged()
    }
}