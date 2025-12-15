package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.unisic_app.R
import com.example.unisic_app.ui.viewmodel.QuizViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private val viewModel: QuizViewModel by viewModels()

    // Views do Layout
    private lateinit var textPontuacao: TextView
    private lateinit var  textPergunta: TextView
    private lateinit var layoutOpcoes: LinearLayout
    private lateinit var layoutResultado: LinearLayout
    private lateinit var textResultadoFinal: TextView
    private lateinit var buttonReiniciar: Button


    private lateinit var fabAddQuestion: FloatingActionButton
    private lateinit var textTimer: TextView


    private val botoesOpcao = mutableListOf<Button>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Mapeamento de Views
        textPontuacao = view.findViewById(R.id.text_pontuacao)
        textPergunta = view.findViewById(R.id.text_pergunta)
        layoutOpcoes = view.findViewById(R.id.layout_opcoes)
        layoutResultado = view.findViewById(R.id.layout_resultado)
        textResultadoFinal = view.findViewById(R.id.text_resultado_final)
        buttonReiniciar = view.findViewById(R.id.button_reiniciar_quiz)


        fabAddQuestion = view.findViewById(R.id.fab_add_question)
        textTimer = view.findViewById(R.id.text_quiz_timer)

        // Mapeia os botões de opção dinamicamente
        botoesOpcao.add(view.findViewById(R.id.button_opcao_a))
        botoesOpcao.add(view.findViewById(R.id.button_opcao_b))
        botoesOpcao.add(view.findViewById(R.id.button_opcao_c))
        // Adicionando mapeamento para o quarto botão
        botoesOpcao.add(view.findViewById(R.id.button_opcao_d))

        //Configura os Observadores (Observers)
        configurarObservadores()

        //Configura os Listeners
        buttonReiniciar.setOnClickListener {
            viewModel.reiniciarQuiz()
        }

        configurarListenersOpcoes()

        // Listener do FAB
        fabAddQuestion.setOnClickListener {
            findNavController().navigate(R.id.action_quizFragment_to_submitQuestionFragment)
        }
    }

    private fun configurarListenersOpcoes() {
        botoesOpcao.forEach { button ->
            button.setOnClickListener {
                val resposta = (it as Button).text.toString()
                viewModel.verificarResposta(resposta)
            }
        }
    }

    private fun configurarObservadores() {
        viewModel.pontuacao.observe(viewLifecycleOwner) { pontuacao ->
            textPontuacao.text = getString(R.string.quiz_score, pontuacao)
        }

        viewModel.tempoRestante.observe(viewLifecycleOwner) { tempo ->
            textTimer.text = getString(R.string.quiz_timer, tempo)
        }

        viewModel.perguntaAtual.observe(viewLifecycleOwner) { pergunta ->
            if (pergunta != null) {
                textPergunta.text = getString(
                    R.string.quiz_question_and_creator,
                    pergunta.questionText,
                    pergunta.creatorNickname
                )

                // Exibe e preenche apenas as opções que existem na lista
                pergunta.options.forEachIndexed { index, opcao ->
                    if (index < botoesOpcao.size) {
                        botoesOpcao[index].text = opcao
                        botoesOpcao[index].visibility = View.VISIBLE
                    }
                }

                //Esconde botões extras se a pergunta tiver menos opções
                for (i in pergunta.options.size until botoesOpcao.size) {
                    botoesOpcao[i].visibility = View.GONE
                }

                layoutOpcoes.visibility = View.VISIBLE
                layoutResultado.visibility = View.GONE
            }
        }

        viewModel.quizConcluido.observe(viewLifecycleOwner) { concluido ->
            if (concluido) {
                mostrarResultadoFinal()
            }
        }
    }

    private fun mostrarResultadoFinal() {
        layoutOpcoes.visibility = View.GONE
        layoutResultado.visibility = View.VISIBLE

        val pontuacaoFinal = viewModel.pontuacao.value ?: 0
        val totalPerguntas = viewModel.totalPerguntas

        textResultadoFinal.text = getString(
            R.string.quiz_final_score,
            pontuacaoFinal,
            totalPerguntas
        )
    }
}