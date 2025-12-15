package com.example.unisic_app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unisic_app.MainActivity
import com.example.unisic_app.R
import com.example.unisic_app.data.repository.FirebaseRepository

class AuthActivity : AppCompatActivity() {

    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Verifica se o usuário já está logado
        if (repository.getCurrentUserId() != null) {
            navigateToMainApp()
        }
    }

    fun navigateToMainApp() {
        // Redireciona o usuário para a tela principal (MainActivity)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}