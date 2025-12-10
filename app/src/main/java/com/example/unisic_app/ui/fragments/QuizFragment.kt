package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Pergunta
import com.example.unisic_app.ui.viewmodel.QuizViewModel

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    // Inicializa o ViewModel (usa a injeção padrão do Fragment KTX)
    private val viewModel: QuizViewModel by viewModels()

    // Views do Layout
    private lateinit var textPontuacao: TextView
    private lateinit var textPergunta: TextView
    private lateinit var layoutOpcoes: LinearLayout
    private lateinit var layoutResultado: LinearLayout
    private lateinit var textResultadoFinal: TextView
    private lateinit var buttonReiniciar: Button

    // Lista de botões para facilitar o mapeamento
    private val botoesOpcao = mutableListOf<Button>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Mapeamento de Views
        textPontuacao = view.findViewById(R.id.text_pontuacao)
        textPergunta = view.findViewById(R.id.text_pergunta)
        layoutOpcoes = view.findViewById(R.id.layout_opcoes)
        layoutResultado = view.findViewById(R.id.layout_resultado)
        textResultadoFinal = view.findViewById(R.id.text_resultado_final)
        buttonReiniciar = view.findViewById(R.id.button_reiniciar_quiz)

        // Mapeia os botões de opção dinamicamente
        botoesOpcao.add(view.findViewById(R.id.button_opcao_a))
        botoesOpcao.add(view.findViewById(R.id.button_opcao_b))
        botoesOpcao.add(view.findViewById(R.id.button_opcao_c))

        // 2. Configura os Observadores (Observers)
        configurarObservadores()

        // 3. Configura o Listener do Botão Reiniciar
        buttonReiniciar.setOnClickListener {
            viewModel.reiniciarQuiz()
        }

        // 4. Configura os Listeners dos Botões de Opção
        configurarListenersOpcoes()
    }

    private fun configurarListenersOpcoes() {
        botoesOpcao.forEach { button ->
            button.setOnClickListener {
                val resposta = (it as Button).text.toString()
                // Chama a lógica de verificação no ViewModel
                viewModel.verificarResposta(resposta)
            }
        }
    }

    private fun configurarObservadores() {
        // Observa o estado da pontuação
        viewModel.pontuacao.observe(viewLifecycleOwner) { pontuacao ->
            textPontuacao.text = getString(R.string.quiz_score, pontuacao)
            // 💡 Nota: Você precisará adicionar o recurso string 'quiz_score' em res/values/strings.xml
        }

        // Observa a pergunta atual e atualiza a UI
        viewModel.perguntaAtual.observe(viewLifecycleOwner) { pergunta ->
            if (pergunta != null) {
                // Atualiza o texto da pergunta
                textPergunta.text = pergunta.texto

                // Atualiza os textos dos botões
                pergunta.opcoes.forEachIndexed { index, opcao ->
                    if (index < botoesOpcao.size) {
                        botoesOpcao[index].text = opcao
                        botoesOpcao[index].visibility = View.VISIBLE
                    }
                }

                // Esconde a tela de resultado
                layoutOpcoes.visibility = View.VISIBLE
                layoutResultado.visibility = View.GONE
            }
        }

        // Observa o status de conclusão do Quiz
        viewModel.quizConcluido.observe(viewLifecycleOwner) { concluido ->
            if (concluido) {
                // Se concluído, mostra a tela de resultado
                mostrarResultadoFinal()
            }
        }
    }

    private fun mostrarResultadoFinal() {
        // Oculta as opções e mostra o resultado
        layoutOpcoes.visibility = View.GONE
        layoutResultado.visibility = View.VISIBLE

        val pontuacaoFinal = viewModel.pontuacao.value ?: 0
        // O total de perguntas agora é acessível após a correção do ViewModel (Getter)
        val totalPerguntas = viewModel.totalPerguntas

        textResultadoFinal.text = getString(
            R.string.quiz_final_score,
            pontuacaoFinal, // Parâmetro 1: Pontuação
            totalPerguntas  // 🌟 Parâmetro 2: Total de Perguntas
        )
    }
}