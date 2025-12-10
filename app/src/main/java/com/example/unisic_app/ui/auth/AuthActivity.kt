package com.example.unisic_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unisic_app.MainActivity
import com.example.unisic_app.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isLoginMode = true // Estado inicial: Login

    // Views
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputSenha: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var textAlternarCadastro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Inicializa o Firebase Auth
        auth = Firebase.auth

        // Mapeamento de Views
        inputEmail = findViewById(R.id.input_email)
        inputSenha = findViewById(R.id.input_senha)
        buttonLogin = findViewById(R.id.button_login)
        textAlternarCadastro = findViewById(R.id.text_alternar_cadastro)

        // Lógica de Botões
        buttonLogin.setOnClickListener {
            if (isLoginMode) {
                // Tenta fazer Login
                performLogin()
            } else {
                // Tenta fazer Cadastro
                performSignup()
            }
        }

        // Alterna entre Login e Cadastro
        textAlternarCadastro.setOnClickListener {
            alternarModo()
        }
    }

    private fun alternarModo() {
        isLoginMode = !isLoginMode
        if (isLoginMode) {
            buttonLogin.text = "ENTRAR"
            textAlternarCadastro.text = "Não tem conta? Cadastre-se aqui."
            // Limpar campos
            inputEmail.setText("")
            inputSenha.setText("")
        } else {
            buttonLogin.text = "CADASTRAR"
            textAlternarCadastro.text = "Já tem conta? Faça login."
        }
    }

    private fun performLogin() {
        val email = inputEmail.text.toString()
        val senha = inputSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    // Login falhou
                    Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun performSignup() {
        val email = inputEmail.text.toString()
        val senha = inputSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Cadastro bem-sucedido
                    Toast.makeText(this, "Cadastro efetuado! Faça login.", Toast.LENGTH_LONG).show()
                    alternarModo() // Volta para a tela de login
                } else {
                    // Cadastro falhou (ex: senha fraca, email já em uso)
                    Toast.makeText(this, "Falha no cadastro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // 🌟 Check the login status on start
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Se já estiver logado, vai direto para a tela principal
            navigateToMain()
        }
    }
}