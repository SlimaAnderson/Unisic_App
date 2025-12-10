package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.repository.FirebaseRepository
import com.example.unisic_app.ui.adapter.CursosAdapter

class CursosFragment : Fragment(R.layout.fragment_cursos) {

    // Supondo que o ID do seu RecyclerView no fragment_cursos.xml é 'recycler_view_cursos'

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Obter a lista de módulos do Repositório (dados estáticos)
        val listaModulos = FirebaseRepository().getModulosCurso()

        // 2. Obter o NavController (Necessário para a navegação no clique do Adapter)
        val navController = findNavController()

        // 3. Encontrar o RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_cursos)

        // 4. Configurar e Conectar
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CursosAdapter(listaModulos, navController)
    }
}