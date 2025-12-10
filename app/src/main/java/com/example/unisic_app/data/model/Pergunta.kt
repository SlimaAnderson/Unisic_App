package com.example.unisic_app.data.model

// Modelo para as perguntas do Quiz
data class Pergunta(
    // Adicionar valores padrão para cada campo
    val id: Int = 0,
    val texto: String = "",
    val opcoes: List<String> = emptyList(), // Lista vazia como padrão
    val respostaCorreta: String = ""
)