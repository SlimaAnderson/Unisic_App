package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unisic_app.R
import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Progresso
import com.example.unisic_app.data.repository.FirebaseRepository
import com.example.unisic_app.ui.adapter.CursosAdapter
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class CursosFragment : Fragment(R.layout.fragment_cursos) {

    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()

    private var listaModulos: List<ModuloCurso> = emptyList()
    private lateinit var recyclerView: RecyclerView
    private var cursosAdapter: CursosAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view_cursos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 1. Carrega os módulos estáticos
        listaModulos = repository.getModulosCurso()

        // Inicializa o Adaptador AQUI com a lista estática
        if (cursosAdapter == null) {
            val navController = findNavController()
            // Liga o Adaptador à RecyclerView para que nunca fique sem um
            cursosAdapter = CursosAdapter(listaModulos, emptyList(), navController)
            recyclerView.adapter = cursosAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarrega o progresso toda vez que a tela é retomada (após voltar do detalhe)
        loadProgressAndSetupAdapter()
    }

    private fun loadProgressAndSetupAdapter() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            // Se deslogado, apenas garante que a lista de módulos (sem progresso) está visível
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