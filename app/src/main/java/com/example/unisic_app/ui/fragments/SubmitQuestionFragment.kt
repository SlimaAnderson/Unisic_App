package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.util.Log
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
    // 🌟 CORREÇÃO 1: Removida a referência incorreta a RadioGroup

    // Views dos Botões de Rádio e Controles
    private lateinit var radioA: RadioButton
    private lateinit var radioB: RadioButton
    private lateinit var radioC: RadioButton
    private lateinit var radioD: RadioButton
    private lateinit var buttonSubmit: Button

    // 🌟 CORREÇÃO 2: Variável de estado global reintroduzida para rastrear o clique manual
    private var checkedRadioId: Int = -1

    // Lista de layouts clicáveis e radio buttons para gerenciamento
    private val layoutOptions = mutableListOf<LinearLayout>()
    private val radioButtons = mutableListOf<RadioButton>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Mapeamento de Views
        textCreatorNick = view.findViewById(R.id.text_creator_nick)
        inputQuestionText = view.findViewById(R.id.input_question_text)

        inputOptionA = view.findViewById(R.id.input_option_a)
        inputOptionB = view.findViewById(R.id.input_option_b)
        inputOptionC = view.findViewById(R.id.input_option_c)
        inputOptionD = view.findViewById(R.id.input_option_d)

        // 🌟 CORREÇÃO 3: Removido o mapeamento incorreto da RadioGroup

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
     * Atualiza a variável de estado global para a validação.
     */
    private fun setOptionListeners() {
        radioButtons.forEach { radio ->
            radio.setOnClickListener {
                // Ao clicar diretamente no RadioButton, garantimos que apenas este é selecionado
                checkedRadioId = it.id // Atualiza o ID global
                radioButtons.forEach { otherRadio ->
                    if (otherRadio.id != it.id) {
                        otherRadio.isChecked = false
                    }
                }
            }
        }

        // Mantém a lógica de clique no layout pai para facilitar a usabilidade
        layoutOptions.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                radioButtons[index].performClick()
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

        // 🌟 CORREÇÃO 4: Usa a variável de estado global atualizada pelos listeners
        val selectedRadioId = checkedRadioId

        // 1. Validação de Campos Vazios
        if (questionText.isEmpty() || options.any { it.isEmpty() }) {
            Toast.makeText(context, "Preencha todos os campos da pergunta e opções.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Determinar o índice correto e Validação da Seleção
        val correctAnswerIndex = when (selectedRadioId) {
            R.id.radio_a -> 0
            R.id.radio_b -> 1
            R.id.radio_c -> 2
            R.id.radio_d -> 3
            else -> {
                // Se checkedRadioId for -1 (nenhum RadioButton selecionado), a validação falha aqui.
                Toast.makeText(context, "Por favor, selecione a resposta correta (A, B, C ou D).", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Validação de Nickname
        val creator = currentNickname
        if (creator == null) {
            Toast.makeText(context, "Erro: Usuário não autenticado ou apelido não carregado.", Toast.LENGTH_LONG).show()
            return
        }

        // 3. Criar e Submeter o objeto Pergunta
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
                Toast.makeText(context, "Pergunta submetida com sucesso!", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            },
            onFailure = { e ->
                Log.e("SubmitQuestion", "Erro ao salvar: ${e.message}")
                Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                buttonSubmit.isEnabled = true
            }
        )
    }
}