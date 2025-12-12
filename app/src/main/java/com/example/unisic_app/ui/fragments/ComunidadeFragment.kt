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

    // 1. Inicializa o ViewModel usando KTX (necessita da dependência lifecycle-viewmodel-ktx)
    private val viewModel: ForumViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var postagemAdapter: PostagemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Inicializar Adapter e RecyclerView
        val navController = findNavController()

        // O Adapter é inicializado com uma lista vazia, pois os dados virão do LiveData.
        postagemAdapter = PostagemAdapter(emptyList(), navController)

        recyclerView = view.findViewById(R.id.recycler_view_forum)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = postagemAdapter

        // 3. Observar o LiveData (Atualiza automaticamente o RecyclerView)
        viewModel.postagens.observe(viewLifecycleOwner) { posts ->
            // A função updateList deve ser implementada no PostagemAdapter
            // para receber a nova lista de Postagem e chamar notifyDataSetChanged().
            postagemAdapter.updateList(posts)
        }

        // 4. Conectar o Floating Action Button (FAB)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_post)

        fab?.setOnClickListener {
            // Navega para a tela de Nova Postagem (ação verificada no nav_graph.xml)
            navController.navigate(R.id.action_comunidadeFragment_to_novaPostagemFragment)
        }
    }
}