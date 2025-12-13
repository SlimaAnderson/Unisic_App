package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Progresso
import com.example.unisic_app.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth

class CursoDetalheFragment : Fragment(R.layout.fragment_curso_detalhe) {

    private val repository = FirebaseRepository()

    private lateinit var textTitulo: TextView
    private lateinit var textConteudo: TextView // 🌟 CORREÇÃO 1: Declaração correta e única
    private lateinit var buttonMarcarConcluido: Button

    private var moduleIdString: String = "0"
    private var isModuleOfficiallyCompleted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moduloIdInt = arguments?.getInt("moduloId") ?: 0
        moduleIdString = moduloIdInt.toString()

        // Lê o status de conclusão instantaneamente dos argumentos (Passo anterior)
        val isPreviouslyCompleted = arguments?.getBoolean("isAlreadyCompleted") ?: false
        isModuleOfficiallyCompleted = isPreviouslyCompleted

        textTitulo = view.findViewById(R.id.text_curso_detalhe_titulo)
        textConteudo = view.findViewById(R.id.text_curso_detalhe_conteudo) // 🌟 CORREÇÃO 2: Mapeamento correto
        buttonMarcarConcluido = view.findViewById(R.id.button_marcar_concluido)

        if (moduloIdInt > 0) {
            val modulo = repository.getModuloCurso(moduloIdInt)

            if (modulo != null) {
                textTitulo.text = modulo.titulo
                textConteudo.text = modulo.conteudo // 🌟 CORREÇÃO 3: Uso correto

                // Atualiza a UI imediatamente com o status do argumento
                if (isModuleOfficiallyCompleted) {
                    buttonMarcarConcluido.text = "MÓDULO CONCLUÍDO"
                    buttonMarcarConcluido.isEnabled = false
                } else {
                    buttonMarcarConcluido.setOnClickListener {
                        markModuleAsCompleted(moduleIdString)
                    }
                }

            } else {
                textTitulo.text = "Módulo não encontrado"
            }
        }

        // Lógica para interceptar o botão "Voltar" (Gesto ou tecla física)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToCursosFragment()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onPause() {
        super.onPause()

        // IMPEDE A SOBRESCRITA: Se a flag for TRUE (carregada do argumento), o onPause() é ignorado.
        if (moduleIdString != "0" && !isModuleOfficiallyCompleted) {
            saveCurrentProgress(moduleIdString, "Em Progresso (Detalhe)")
        }
    }


    private fun navigateToCursosFragment() {
        findNavController().navigate(
            R.id.cursosFragment,
            null,
            navOptions {
                popUpTo(R.id.cursosFragment) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        )
    }

    private fun saveCurrentProgress(moduleId: String, currentSectionName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val progress = Progresso(
            moduleId = moduleId,
            lastSection = currentSectionName,
            completed = false
        )

        repository.saveCourseProgress(userId, progress,
            onSuccess = {
                Log.d("Progress", "Progresso salvo automaticamente para $moduleId")
            },
            onFailure = { error ->
                Log.e("Progress", "Erro ao salvar progresso para $moduleId: ${error.message}")
            }
        )
    }

    private fun markModuleAsCompleted(moduleId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val progress = Progresso(
            moduleId = moduleId,
            lastSection = "Finalizado",
            completed = true
        )

        buttonMarcarConcluido.isEnabled = false

        repository.saveCourseProgress(userId, progress,
            onSuccess = {
                Toast.makeText(requireContext(), "Módulo finalizado e registrado!", Toast.LENGTH_LONG).show()

                isModuleOfficiallyCompleted = true

                navigateToCursosFragment()
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "Erro ao concluir módulo: ${error.message}", Toast.LENGTH_LONG).show()
                buttonMarcarConcluido.isEnabled = true
            }
        )
    }
}