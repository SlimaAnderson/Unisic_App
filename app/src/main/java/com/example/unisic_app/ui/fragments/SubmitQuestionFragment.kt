// ui/fragments/SubmitQuestionFragment.kt

package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Pergunta
import com.example.unisic_app.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth

class SubmitQuestionFragment : Fragment(R.layout.fragment_submit_question) {

    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()
    private var currentNickname: String? = null

    // Views de Entrada
    private lateinit var textCreatorNick: TextView
    private lateinit var inputQuestionText: EditText
    private lateinit var inputOptionA: EditText
    private lateinit var inputOptionB: EditText
    private lateinit var inputOptionC: EditText
    private lateinit var inputOptionD: EditText

    // Views dos Botões de Rádio e Controles
    private lateinit var radioA: RadioButton
    private lateinit var radioB: RadioButton
    private lateinit var radioC: RadioButton
    private lateinit var radioD: RadioButton
    private lateinit var buttonSubmit: Button

    // Variável para rastrear o ID do Radio Button selecionado
    private var checkedRadioId: Int = -1

    // Lista de layouts clicáveis e radio buttons para gerenciamento
    private val layoutOptions = mutableListOf<LinearLayout>()
    private val radioButtons = mutableListOf<RadioButton>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Mapeamento de Views
        // OBS: Estes IDs agora existem no XML corrigido.
        textCreatorNick = view.findViewById(R.id.text_creator_nick)
        inputQuestionText = view.findViewById(R.id.input_question_text)

        inputOptionA = view.findViewById(R.id.input_option_a)
        inputOptionB = view.findViewById(R.id.input_option_b)
        inputOptionC = view.findViewById(R.id.input_option_c)
        inputOptionD = view.findViewById(R.id.input_option_d)

        radioA = view.findViewById(R.id.radio_a)
        radioB = view.findViewById(R.id.radio_b)
        radioC = view.findViewById(R.id.radio_c)
        radioD = view.findViewById(R.id.radio_d)

        buttonSubmit = view.findViewById(R.id.button_submit_question)

        // Mapeia os layouts das opções (Layouts clicáveis: layout_option_a, etc.)
        layoutOptions.add(view.findViewById(R.id.layout_option_a))
        layoutOptions.add(view.findViewById(R.id.layout_option_b))
        layoutOptions.add(view.findViewById(R.id.layout_option_c))
        layoutOptions.add(view.findViewById(R.id.layout_option_d))

        radioButtons.addAll(listOf(radioA, radioB, radioC, radioD))

        // 2. Configura a lógica de seleção de rádio e listeners de submissão
        setOptionListeners()
        loadCurrentUserNickname()

        buttonSubmit.setOnClickListener {
            submitQuestion()
        }
    }

    /**
     * Gerencia a seleção exclusiva de Radio Buttons quando o usuário clica no LinearLayout pai.
     * Isso resolve a falha de detecção do RadioGroup nativo.
     */
    private fun setOptionListeners() {
        layoutOptions.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                val clickedRadio = radioButtons[index]

                // Desmarca todos os outros manualmente
                radioButtons.forEach { radio ->
                    if (radio != clickedRadio) {
                        radio.isChecked = false
                    }
                }

                // Marca o clicado e atualiza o ID rastreado
                clickedRadio.isChecked = true
                checkedRadioId = clickedRadio.id // Atualiza o ID global
            }
        }
    }

    private fun loadCurrentUserNickname() {
        val user = auth.currentUser
        if (user != null) {
            currentNickname = user.displayName ?: user.email?.split("@")?.get(0) ?: "Anônimo"
            textCreatorNick.text = "Criador: @$currentNickname"
        } else {
            currentNickname = null
            textCreatorNick.text = "Criador: Desconectado"
            buttonSubmit.isEnabled = false
            Toast.makeText(context, "Faça login para submeter perguntas.", Toast.LENGTH_LONG).show()
        }
    }

    private fun submitQuestion() {
        val questionText = inputQuestionText.text.toString().trim()
        val options = listOf(
            inputOptionA.text.toString().trim(),
            inputOptionB.text.toString().trim(),
            inputOptionC.text.toString().trim(),
            inputOptionD.text.toString().trim()
        )

        // 1. Validação de Conteúdo (Textos)
        if (questionText.isEmpty() || options.any { it.isEmpty() } || currentNickname == null) {
            Toast.makeText(context, "Preencha todos os campos de texto.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validação e Determinação do Índice Correto
        // Se o checkedRadioId for -1 (inicial) ou não corresponder a um ID válido,
        // ele cairá no "else", mostrando a mensagem de erro.
        val correctAnswerIndex = when (checkedRadioId) {
            R.id.radio_a -> 0
            R.id.radio_b -> 1
            R.id.radio_c -> 2
            R.id.radio_d -> 3
            else -> {
                Toast.makeText(context, "Por favor, selecione a resposta correta (A, B, C ou D).", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val creator = currentNickname!!

        // 3. Criar e Submeter
        val newQuestion = Pergunta(
            questionText = questionText,
            options = options,
            correctAnswerIndex = correctAnswerIndex,
            category = "Comunidade",
            creatorNickname = creator
        )

        // 4. Submeter ao Repositório
        buttonSubmit.isEnabled = false
        repository.submitUserQuestion(newQuestion,
            onSuccess = {
                Toast.makeText(context, "Pergunta submetida com sucesso! Redirecionando...", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            },
            onFailure = { e ->
                Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                buttonSubmit.isEnabled = true
            }
        )
    }
}