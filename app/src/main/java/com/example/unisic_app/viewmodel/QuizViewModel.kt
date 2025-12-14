// ui/viewmodel/QuizViewModel.kt

package com.example.unisic_app.ui.viewmodel

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unisic_app.data.model.Pergunta
import com.example.unisic_app.data.repository.FirebaseRepository

class QuizViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    // --- LiveData de Estado do Quiz ---
    private val _perguntas = MutableLiveData<List<Pergunta>>()
    private val _perguntaAtual = MutableLiveData<Pergunta?>()
    val perguntaAtual: LiveData<Pergunta?> = _perguntaAtual

    private val _pontuacao = MutableLiveData(0)
    val pontuacao: LiveData<Int> = _pontuacao

    private val _quizConcluido = MutableLiveData(false)
    val quizConcluido: LiveData<Boolean> = _quizConcluido

    // --- Timer e Lógica de Dificuldade ---
    private val INITIAL_TIME_PER_QUESTION_SECONDS = 15
    private val TIME_PENALTY_FOR_CORRECT_ANSWER_SECONDS = 1
    private val MIN_TIME_SECONDS = 5

    private var currentCorrectAnswersCount = 0
    private var currentTimer: CountDownTimer? = null

    private val _tempoRestante = MutableLiveData<Int>()
    val tempoRestante: LiveData<Int> = _tempoRestante

    // Variáveis de Controle
    private var indicePerguntaAtual = 0
    val totalPerguntas: Int
        get() = _perguntas.value?.size ?: 0

    init {
        carregarPerguntas()
    }

    private fun carregarPerguntas() {
        currentTimer?.cancel()
        _perguntaAtual.value = null

        repository.getQuizQuestionsOnce(
            onSuccess = { perguntasCarregadas ->
                _perguntas.value = perguntasCarregadas.shuffled()
                indicePerguntaAtual = 0
                _pontuacao.value = 0
                _quizConcluido.value = false
                currentCorrectAnswersCount = 0

                if (perguntasCarregadas.isNotEmpty()) {
                    avancarParaProximaPergunta()
                } else {
                    Log.w("QuizVM", "Nenhuma pergunta carregada do Firestore.")
                    finalizarQuiz()
                }
            },
            onFailure = { e ->
                Log.e("QuizVM", "Falha ao carregar perguntas: ${e.message}")
            }
        )
    }

    private fun avancarParaProximaPergunta() {
        if (indicePerguntaAtual < totalPerguntas) {
            _perguntaAtual.value = _perguntas.value?.get(indicePerguntaAtual)
            indicePerguntaAtual++
            startQuestionTimer()
        } else {
            finalizarQuiz()
        }
    }

    private fun startQuestionTimer() {
        currentTimer?.cancel()

        val penalty = currentCorrectAnswersCount * TIME_PENALTY_FOR_CORRECT_ANSWER_SECONDS

        val durationSeconds = (INITIAL_TIME_PER_QUESTION_SECONDS - penalty)
            .coerceAtLeast(MIN_TIME_SECONDS)

        _tempoRestante.value = durationSeconds

        currentTimer = object : CountDownTimer(durationSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                _tempoRestante.value = secondsRemaining
            }

            override fun onFinish() {
                Log.d("QuizVM", "Tempo esgotado.")
                // 💡 Nota: Aqui estamos passando uma String vazia, pois não houve resposta real
                verificarResposta("", isTimeUp = true)
            }
        }.start()
    }

    fun verificarResposta(respostaUsuario: String, isTimeUp: Boolean = false) {
        currentTimer?.cancel()
        val pergunta = _perguntaAtual.value ?: return

        // 🌟 CORREÇÃO 1: Fazer a conversão segura de String? para Int.
        // O campo agora é String? devido ao erro de desserialização anterior.
        val indiceCorreto = pergunta.correctAnswerIndex?.toIntOrNull()

        // Se o índice correto não puder ser convertido (indiceCorreto == null),
        // assumimos que a pergunta é inválida e o usuário errou, ou simplesmente retornamos.
        if (indiceCorreto == null) {
            Log.e("QuizVM", "correctAnswerIndex inválido para conversão Int: ${pergunta.correctAnswerIndex}")
        }

        // 🌟 CORREÇÃO 2: Obter o texto da resposta correta usando o índice convertido
        val respostaCorretaTexto = if (indiceCorreto != null) {
            pergunta.options.getOrNull(indiceCorreto)
        } else {
            null
        }

        // 🌟 CORREÇÃO 3: Lógica de verificação
        // Aumenta a pontuação se não for tempo esgotado E a resposta do usuário corresponder ao texto da opção correta.
        if (!isTimeUp && respostaUsuario == respostaCorretaTexto) {
            _pontuacao.value = (_pontuacao.value ?: 0) + 1
            currentCorrectAnswersCount++
        }

        Handler(Looper.getMainLooper()).postDelayed({
            avancarParaProximaPergunta()
        }, 800)
    }

    private fun finalizarQuiz() {
        currentTimer?.cancel()
        _quizConcluido.value = true
    }

    fun reiniciarQuiz() {
        carregarPerguntas()
    }

    override fun onCleared() {
        super.onCleared()
        currentTimer?.cancel()
    }
}