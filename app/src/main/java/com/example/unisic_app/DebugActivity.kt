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
import com.example.unisic_app.MainActivity // Certifique-se de que este import existe

class DebugActivity : AppCompatActivity() {

    private lateinit var textLog: TextView
    private val logMessages = StringBuilder()

    // Variável do Repositório (Mantida apenas uma vez)
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

        // 🌟 CORREÇÃO: Chama a função que contém a lógica de teste assíncrona
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

    // 🌟 CORREÇÃO: Função reconstruída para testes assíncronos do Firestore
    private fun testStaticDataLoading() {
        log("-> Iniciando teste de carregamento de Notícias (Firestore)...")

        repository.getNoticiasOnce(
            onSuccess = { noticias ->
                val count = noticias.size
                log("-> Notícias carregadas: ${count} itens. [SUCESSO]")

                // Testa o carregamento de Vagas em seguida (se necessário)
                testVagasLoading()
            },
            onFailure = { e ->
                log("[ERRO] Falha na leitura de Notícias: ${e.message}")
                log("Verifique se há dados na coleção 'noticias' no Firestore.")
                log("Testes de conteúdo estático concluídos. Clique para iniciar o app.")
            }
        )
    }

    // 🌟 NOVO: Função para testar o carregamento de Vagas
    private fun testVagasLoading() {
        log("-> Iniciando teste de carregamento de Vagas (Firestore)...")

        // Assumindo que você também criou getVagasEmpregoOnce no repositório
        repository.getVagasEmpregoOnce(
            onSuccess = { vagas ->
                val count = vagas.size
                log("-> Vagas carregadas: ${count} itens. [SUCESSO]")
                log("Testes de conteúdo estático concluídos. Clique para iniciar o app.")
            },
            onFailure = { e ->
                log("[ERRO] Falha na leitura de Vagas: ${e.message}")
                log("Verifique se há dados na coleção 'vagas' no Firestore.")
                log("Testes de conteúdo estático concluídos. Clique para iniciar o app.")
            }
        )
    }
}