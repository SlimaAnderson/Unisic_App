package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.ui.adapter.PostagemAdapter
import com.example.unisic_app.ui.viewmodel.ForumViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ComunidadeFragment : Fragment(R.layout.fragment_comunidade) {

    // Inicializa o ViewModel usando KTX
    private val viewModel: ForumViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var postagemAdapter: PostagemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Adapter e RecyclerView
        val navController = findNavController()

        // Adapter é inicializado com uma lista vazia, pois os dados virão do LiveData.
        postagemAdapter = PostagemAdapter(emptyList(), navController)

        recyclerView = view.findViewById(R.id.recycler_view_forum)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = postagemAdapter

        // bservar o LiveData (Atualiza automaticamente o RecyclerView)
        viewModel.postagens.observe(viewLifecycleOwner) { posts ->

            // para receber a nova lista de Postagem e chamar notifyDataSetChanged().
            postagemAdapter.updateList(posts)
        }

        // Conectar o Floating Action Button (FAB)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_post)

        fab?.setOnClickListener {
            // Navega para a tela de Nova Postagem
            navController.navigate(R.id.action_comunidadeFragment_to_novaPostagemFragment)
        }
    }
}