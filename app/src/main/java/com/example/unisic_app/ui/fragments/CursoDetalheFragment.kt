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
import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Progresso
import com.example.unisic_app.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
// IMPORTANTE: Se estiver usando SafeArgs, certifique-se de que a importação do Args está correta e a classe foi gerada.
// import com.example.unisic_app.ui.fragments.CursoDetalheFragmentArgs

class CursoDetalheFragment : Fragment(R.layout.fragment_curso_detalhe) {

    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var textTitulo: TextView
    private lateinit var textSubtitulo: TextView
    private lateinit var textConteudo: TextView
    private lateinit var buttonMarcarConcluido: Button

    // 💡 Agora armazena o ID como String, que é o que o repositório Firebase espera para o document().
    private var moduleIdString: String = ""
    private var isModuleOfficiallyCompleted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. OBTENDO O ARGUMENTO CORRETAMENTE

        // 🚨 CORREÇÃO PRINCIPAL: Lê o argumento como String, conforme enviado pelo Adapter.
        val moduleIdFromArgs = arguments?.getString("moduloId")
        val isPreviouslyCompleted = arguments?.getBoolean("isAlreadyCompleted") ?: false

        // CORREÇÃO: Mapeamento de Views
        textTitulo = view.findViewById(R.id.text_curso_detalhe_titulo)
        textSubtitulo = view.findViewById(R.id.text_curso_detalhe_subtitulo)
        textConteudo = view.findViewById(R.id.text_curso_detalhe_conteudo)
        buttonMarcarConcluido = view.findViewById(R.id.button_marcar_concluido)


        // 2. VALIDAÇÃO
        // Verifica se a String do ID é nula ou vazia.
        if (moduleIdFromArgs.isNullOrEmpty()) {
            // Mostra a mensagem e sai da tela (popBackStack)
            Toast.makeText(requireContext(), "Erro: ID do módulo não fornecido (String inválida).", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // Armazena a String recebida.
        moduleIdString = moduleIdFromArgs
        isModuleOfficiallyCompleted = isPreviouslyCompleted

        // 3. Carregar o conteúdo do Módulo do Firebase usando a String do ID
        loadModuleContent(moduleIdString)

        // Lógica para interceptar o botão "Voltar" (Gesto ou tecla física)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToCursosFragment()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun loadModuleContent(moduleId: String) {
        repository.getModuloCursoById(moduleId,
            onSuccess = { modulo ->
                if (modulo != null) {
                    // Preenche os TextViews
                    textTitulo.text = modulo.titulo
                    textSubtitulo.text = modulo.subtitulo
                    textConteudo.text = modulo.conteudo

                    setupButton(isModuleOfficiallyCompleted)
                } else {
                    textTitulo.text = "Módulo não encontrado"
                    textConteudo.text = "O conteúdo deste módulo pode ter sido removido."
                }
            },
            onFailure = { e ->
                Log.e("CursoDetalhe", "Falha ao carregar módulo $moduleId: ${e.message}")
                Toast.makeText(requireContext(), "Erro ao carregar conteúdo.", Toast.LENGTH_SHORT).show()
                textTitulo.text = "Erro de Carregamento"
            }
        )
    }

    private fun setupButton(isCompleted: Boolean) {
        if (isCompleted) {
            buttonMarcarConcluido.text = "MÓDULO CONCLUÍDO"
            buttonMarcarConcluido.isEnabled = false
        } else {
            buttonMarcarConcluido.text = "MARCAR COMO CONCLUÍDO"
            buttonMarcarConcluido.isEnabled = true
            buttonMarcarConcluido.setOnClickListener {
                markModuleAsCompleted(moduleIdString)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // IMPEDE A SOBRESCRITA: Se a flag for TRUE (carregada do argumento), o onPause() é ignorado.
        if (moduleIdString.isNotEmpty() && !isModuleOfficiallyCompleted) {
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
        val userId = auth.currentUser?.uid ?: return

        // O Progresso espera um moduleId (String)
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
        val userId = auth.currentUser?.uid ?: return

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