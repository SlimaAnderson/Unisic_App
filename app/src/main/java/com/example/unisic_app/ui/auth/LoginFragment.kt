// ui/auth/LoginFragment.kt

package com.example.unisic_app.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unisic_app.R
import com.example.unisic_app.data.repository.FirebaseRepository

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val repository = FirebaseRepository()

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referência às Views
        inputEmail = view.findViewById(R.id.input_login_email)
        inputPassword = view.findViewById(R.id.input_login_password)
        btnLogin = view.findViewById(R.id.button_login)
        btnGoToRegister = view.findViewById(R.id.button_go_to_register)

        btnLogin.setOnClickListener {
            performLogin()
        }

        btnGoToRegister.setOnClickListener {
            // Navega para o Fragmento de Cadastro
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun performLogin() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        repository.loginUser(email, password,
            onSuccess = {
                // Login bem-sucedido: Redireciona para a Activity principal
                (activity as? AuthActivity)?.navigateToMainApp()
            },
            onFailure = { errorMessage ->
                Toast.makeText(context, "Falha no Login: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }
}