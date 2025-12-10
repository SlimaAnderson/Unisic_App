package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.repository.FirebaseRepository
import com.example.unisic_app.ui.adapter.NoticiaAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Obter a lista de notícias do Repositório (dados estáticos)
        val listaNoticias = FirebaseRepository().getNoticias()

        // 2. Encontrar o RecyclerView no layout do Fragment
        // Supondo que você criou o RecyclerView com o ID: 'recycler_view_noticias' no fragment_home.xml
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_noticias)

        // 3. Configurar o RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = NoticiaAdapter(listaNoticias)
    }
}