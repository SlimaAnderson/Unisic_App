package com.example.unisic_app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.VagaEmprego

class VagaAdapter(
    private var listaVagas: List<VagaEmprego>,
    private val clickListener: (VagaEmprego) -> Unit
) : RecyclerView.Adapter<VagaAdapter.VagaViewHolder>() {

    class VagaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.text_vaga_titulo)
        val empresa: TextView = itemView.findViewById(R.id.text_vaga_empresa)
        val local: TextView = itemView.findViewById(R.id.text_vaga_local)
        val data: TextView = itemView.findViewById(R.id.text_vaga_data)

        fun bind(vaga: VagaEmprego, clickListener: (VagaEmprego) -> Unit) {
            titulo.text = vaga.titulo
            empresa.text = "Empresa: ${vaga.empresa}"
            local.text = "Local: ${vaga.localizacao}"
            data.text = "Publicado em: ${vaga.dataPublicacao}"
            itemView.setOnClickListener {
                clickListener(vaga)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VagaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaga, parent, false)
        return VagaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VagaViewHolder, position: Int) {
        holder.bind(listaVagas[position], clickListener)
    }

    override fun getItemCount(): Int = listaVagas.size

    fun updateList(newList: List<VagaEmprego>) {
        listaVagas = newList
        notifyDataSetChanged()
    }
}