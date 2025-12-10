package com.example.unisic_app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unisic_app.data.model.Pergunta
import com.example.unisic_app.data.repository.FirebaseRepository

class QuizViewModel : ViewModel() {

    // Lista de perguntas carregadas do Repositório
    private val perguntas = FirebaseRepository().getPerguntas()

    val totalPerguntas: Int
        get() = perguntas.size// Retorna o tamanho da lista privada

    // Estado do Quiz
    private var indicePerguntaAtual = 0
    private var pontuacaoAtual = 0

    // LiveData que o Fragment irá OBSERVAR para atualizar a UI

    // Contém a Pergunta que deve ser exibida na tela
    private val _perguntaAtual = MutableLiveData<Pergunta>()
    val perguntaAtual: LiveData<Pergunta> = _perguntaAtual

    // Contém a pontuação para ser exibida
    private val _pontuacao = MutableLiveData<Int>()
    val pontuacao: LiveData<Int> = _pontuacao

    // Indica se o Quiz terminou (true/false)
    private val _quizConcluido = MutableLiveData<Boolean>()
    val quizConcluido: LiveData<Boolean> = _quizConcluido


    init {
        // Inicializa a primeira pergunta
        if (perguntas.isNotEmpty()) {
            _perguntaAtual.value = perguntas[indicePerguntaAtual]
            _pontuacao.value = 0
            _quizConcluido.value = false
        } else {
            // Caso não haja perguntas, conclui o quiz imediatamente
            _quizConcluido.value = true
        }
    }

    /**
     * Verifica se a resposta selecionada está correta e avança para a próxima pergunta.
     * Retorna true se a resposta estiver correta.
     */
    fun verificarResposta(respostaSelecionada: String): Boolean {
        val pergunta = _perguntaAtual.value ?: return false

        val correta = respostaSelecionada == pergunta.respostaCorreta

        if (correta) {
            pontuacaoAtual += 10 // Adiciona pontos pela resposta correta
            _pontuacao.value = pontuacaoAtual
        }

        // Prepara a próxima pergunta
        avancarPergunta()

        return correta
    }

    /**
     * Avança para a próxima pergunta na lista ou conclui o quiz.
     */
    private fun avancarPergunta() {
        indicePerguntaAtual++
        if (indicePerguntaAtual < perguntas.size) {
            _perguntaAtual.value = perguntas[indicePerguntaAtual]
        } else {
            // Não há mais perguntas: Quiz Concluído
            _quizConcluido.value = true
        }
    }

    /**
     * Reinicia o quiz para a primeira pergunta.
     */
    fun reiniciarQuiz() {
        indicePerguntaAtual = 0
        pontuacaoAtual = 0
        _pontuacao.value = 0
        _quizConcluido.value = false
        if (perguntas.isNotEmpty()) {
            _perguntaAtual.value = perguntas[indicePerguntaAtual]
        }
    }
}