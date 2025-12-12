// ui/auth/RegisterFragment.kt

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

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val repository = FirebaseRepository()

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputNick: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoToLogin: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputEmail = view.findViewById(R.id.input_register_email)
        inputPassword = view.findViewById(R.id.input_register_password)
        inputNick = view.findViewById(R.id.input_register_nick)
        btnRegister = view.findViewById(R.id.button_register)
        btnGoToLogin = view.findViewById(R.id.button_go_to_login)

        btnRegister.setOnClickListener {
            performRegistration()
        }

        btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun performRegistration() {
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()
        val nick = inputNick.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || nick.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        repository.registerUser(email, password, nick,
            onSuccess = {
                // Navegação após cadastro bem-sucedido: Redireciona para o login
                Toast.makeText(context, "Cadastro realizado com sucesso! Faça o login.", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            },
            onFailure = { errorMessage ->
                Toast.makeText(context, "Falha no Cadastro: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }
}