package com.example.unisic_app.data.repository

import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.model.Noticia
import com.example.unisic_app.data.model.Pergunta
import com.example.unisic_app.data.model.Postagem

// Esta classe simula o acesso a dados.
// No futuro, a lógica aqui será substituída por chamadas assíncronas ao Firebase Firestore.
class FirebaseRepository {

    // --- Módulo Home: Notícias ---
    fun getNoticias(): List<Noticia> {
        return listOf(
            Noticia("n1", "Novo golpe de Pix via WhatsApp - Alerta!", "http://link.para.noticia1"),
            Noticia("n2", "Falha de segurança crítica encontrada no Chrome", "http://link.para.noticia2"),
            Noticia("n3", "Melhores práticas de senhas para 2025", "http://link.para.noticia3")
        )
    }

    // --- Módulo Comunidade: Postagens do Fórum (Estáticas) ---
    fun getPostagensForum(): List<Postagem> {
        return listOf(
            Postagem("p1", "Administrador", "Bem-vindos à comunidade UNISIC! Usem este espaço para tirar dúvidas e compartilhar experiências.", "01/12/2025"),
            Postagem("p2", "Maria S.", "Alguém tem uma boa recomendação de Gerenciador de Senhas gratuito?", "03/12/2025"),
            Postagem("p3", "João P.", "Minha empresa mandou um e-mail estranho, parece Phishing...", "05/12/2025")
        )
    }

    // --- Módulo Quiz: Perguntas ---
    fun getPerguntas(): List<Pergunta> {
        return listOf(
            Pergunta(1, "Qual o maior risco do Phishing?", listOf("Perder acesso Wi-Fi", "Roubo de credenciais ou dados", "Bateria viciar mais rápido"), "Roubo de credenciais ou dados"),
            Pergunta(2, "O que é Autenticação de Dois Fatores (2FA)?", listOf("Usar duas senhas", "Usar senha e um código temporário", "Usar o celular para ligar"), "Usar senha e um código temporário"),
            Pergunta(3, "Qual o melhor lugar para salvar senhas?", listOf("Bloco de notas do celular", "Post-it no monitor", "Gerenciador de Senhas criptografado"), "Gerenciador de Senhas criptografado")
        )
    }

    fun getModuloCurso(id: Int): ModuloCurso? {
        return getModulosCurso().find { it.id == id }
    }

    // --- Módulo Cursos: Módulos ---
    fun getModulosCurso(): List<ModuloCurso> {
        return listOf(
            ModuloCurso(1, "Introdução", "Visão geral da segurança", "A segurança digital é um estado de espírito..."),
            ModuloCurso(2, "Iniciante", "Senhas e 2FA", "Aprenda a criar senhas fortes...")
        )
    }
}