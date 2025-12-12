package com.example.unisic_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unisic_app.data.repository.FirebaseRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DebugActivity : AppCompatActivity() {

    private lateinit var textLog: TextView
    private val logMessages = StringBuilder()
    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        textLog = findViewById(R.id.text_log)
        val buttonStartMain = findViewById<Button>(R.id.button_start_main)

        buttonStartMain.setOnClickListener {
            // Inicia a Activity principal (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Inicia os testes de componentes
        log("Iniciando testes de integração...")
        testFirebaseInitialization()
        testStaticDataLoading()
    }

    private fun log(message: String) {
        logMessages.append(message).append("\n")
        textLog.text = logMessages.toString()
    }

    private fun testFirebaseInitialization() {
        try {
            // Testa se o FirebaseApp foi inicializado
            FirebaseApp.getInstance()
            log("- Firebase Core: OK")

            // Testa a acessibilidade dos serviços
            Firebase.auth
            log("- Firebase Auth: OK")

            Firebase.firestore
            log("- Firebase Firestore: OK")

            // Conclusão
            log("[SUCESSO] Configuração Firebase inicializada.")

        } catch (e: Exception) {
            log("[ERRO FATAL] Falha na inicialização do Firebase: ${e.message}")
            log("Verifique seu google-services.json e dependências.")
        }
    }

    private fun testStaticDataLoading() {
        try {
            // 1. Teste de Notícias
            val noticias = repository.getNoticias()
            log("- Carregando Notícias: ${noticias.size} itens. Status: OK")

            // 3. Teste de Perguntas do Quiz
            val perguntas = repository.getPerguntas()
            log("- Carregando Perguntas do Quiz: ${perguntas.size} itens. Status: OK")

            // 4. Teste de Módulos de Cursos
            val modulos = repository.getModulosCurso()
            log("- Carregando Módulos de Cursos: ${modulos.size} itens. Status: OK")

        } catch (e: Exception) {
            log("[ERRO] Falha na leitura do Repositório: ${e.message}")
            log("Verifique se seus Data Classes têm construtores vazios (valores padrão)!")
        }

        log("Testes de conteúdo estático concluídos. Clique para iniciar o app.")
    }
}