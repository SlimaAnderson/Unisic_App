package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unisic_app.R
import com.example.unisic_app.data.repository.FirebaseRepository

class CursoDetalheFragment : Fragment(R.layout.fragment_curso_detalhe) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Obter o argumento ID passado da lista de cursos
        // O nome 'moduloId' deve corresponder ao argumento que definiremos no nav_graph.xml
        val moduloId = arguments?.getInt("moduloId") ?: 0

        val textTitulo = view.findViewById<TextView>(R.id.text_curso_detalhe_titulo)
        val textConteudo = view.findViewById<TextView>(R.id.text_curso_detalhe_conteudo)

        if (moduloId > 0) {
            // 2. Buscar o módulo detalhado no Repositório usando o ID
            val modulo = FirebaseRepository().getModuloCurso(moduloId)

            if (modulo != null) {
                // 3. Preencher a UI
                textTitulo.text = modulo.titulo
                textConteudo.text = modulo.conteudo
            } else {
                textTitulo.text = "Módulo não encontrado"
            }
        }
    }
}