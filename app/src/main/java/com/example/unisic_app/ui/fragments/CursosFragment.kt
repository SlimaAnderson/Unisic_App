package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Progresso
import com.example.unisic_app.data.repository.FirebaseRepository
import com.example.unisic_app.ui.adapter.CursosAdapter
import com.example.unisic_app.ui.viewmodel.CursosViewModel
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class CursosFragment : Fragment(R.layout.fragment_cursos) {

    //Inicializa o ViewModel para carregar os módulos dinamicamente
    private val viewModel: CursosViewModel by viewModels()

    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()


    private lateinit var recyclerView: RecyclerView
    private var cursosAdapter: CursosAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view_cursos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Inicializa o Adaptador
        if (cursosAdapter == null) {
            val navController = findNavController()
            // Inicializa o Adapter com listas vazias, os dados virão do observe
            cursosAdapter = CursosAdapter(emptyList(), emptyList(), navController)
            recyclerView.adapter = cursosAdapter
        }

        // Observa os Módulos do Firebase (Dados Dinâmicos)
        viewModel.modulos.observe(viewLifecycleOwner) { modulosCarregados ->
            Log.d("CursosFragment", "Atualizando RecyclerView com ${modulosCarregados.size} módulos.")
            // Atualiza a lista de Módulos no Adapter
            cursosAdapter?.updateModulosList(modulosCarregados)

            // Re-carrega o progresso após carregar os módulos, garantindo que o Adapter
            // tem todos os dados para fazer a correspondência.
            loadProgressAndSetupAdapter()
        }

    }

    override fun onResume() {
        super.onResume()

        loadProgressAndSetupAdapter()
    }

    private fun loadProgressAndSetupAdapter() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            cursosAdapter?.updateProgressList(emptyList())
            return
        }

        repository.getProgressForUser(userId,
            onSuccess = { progressoList ->
                // Progresso carregado, atualizamos o adaptador existente
                cursosAdapter?.updateProgressList(progressoList)
            },
            onFailure = { e ->
                Log.e("CursosFragment", "Falha ao carregar progresso: ${e.message}")
                Toast.makeText(requireContext(), "Erro ao carregar seu progresso.", Toast.LENGTH_SHORT).show()
                cursosAdapter?.updateProgressList(emptyList())
            }
        )
    }
}