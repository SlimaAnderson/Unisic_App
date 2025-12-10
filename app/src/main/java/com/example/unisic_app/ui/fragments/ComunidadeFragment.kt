package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.repository.FirebaseRepository
import com.example.unisic_app.ui.adapter.PostagemAdapter

class ComunidadeFragment : Fragment(R.layout.fragment_comunidade) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Obter a lista de postagens (dados estáticos)
        val listaPostagens = FirebaseRepository().getPostagensForum()

        // 2. Encontrar o RecyclerView no layout (ID: recycler_view_forum)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_forum)

        // 3. Configurar e Conectar
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = PostagemAdapter(listaPostagens)
    }
}