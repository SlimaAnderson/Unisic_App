package com.example.unisic_app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Profile
import com.example.unisic_app.data.repository.FirebaseRepository
import com.example.unisic_app.ui.auth.AuthActivity // Para navegação de volta ao login

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val repository = FirebaseRepository()

    private lateinit var textNick: TextView
    private lateinit var inputName: EditText
    private lateinit var inputCompany: EditText
    private lateinit var inputDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    private var profileUid: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar Views
        textNick = view.findViewById(R.id.text_profile_nick)
        inputName = view.findViewById(R.id.input_profile_name)
        inputCompany = view.findViewById(R.id.input_profile_company)
        inputDescription = view.findViewById(R.id.input_profile_description)
        btnSave = view.findViewById(R.id.button_save_profile)
        btnLogout = view.findViewById(R.id.button_logout)

        // 2. Determinar o UID a ser usado: PRIORIDADE TOTAL para o argumento "profileUid"
        val myUid = repository.getCurrentUserId()

        // 🌟 CORREÇÃO DE LEITURA: Pega o UID passado pelo comentário
        val targetUid = arguments?.getString("profileUid")

        // Define o UID que será carregado. Se targetUid existir, usa ele.
        profileUid = targetUid ?: myUid

        if (profileUid == null) {
            performLogoutNavigation()
            return
        }

        val isViewingOwnProfile = profileUid == myUid

        // 3. 🌟 AJUSTE CRÍTICO DE VISIBILIDADE E EDIÇÃO 🌟
        if (!isViewingOwnProfile) {
            // Perfil de Terceiros: Desabilita todos os campos de input e oculta botões
            inputName.isEnabled = false
            inputCompany.isEnabled = false
            inputDescription.isEnabled = false
            btnSave.visibility = View.GONE
            btnLogout.visibility = View.GONE
            activity?.title = "Perfil do Usuário"
            // Adiciona uma mensagem para deixar claro que a bio não é editável
            inputDescription.hint = "Bio (Não editável)"
            inputName.hint = "Nome"

        } else {
            // Meu Próprio Perfil: Habilita tudo
            btnSave.visibility = View.VISIBLE
            btnLogout.visibility = View.VISIBLE
            inputName.isEnabled = true
            inputCompany.isEnabled = true
            inputDescription.isEnabled = true
            activity?.title = "Meu Perfil"
        }

        // 4. Carregar o perfil com base no UID alvo (profileUid)
        loadProfileData(profileUid!!)

        // 5. Listeners (Só funcionam se for o próprio perfil)
        if (isViewingOwnProfile) {
            btnSave.setOnClickListener {
                saveProfileData(profileUid!!)
            }

            btnLogout.setOnClickListener {
                repository.logoutUser()
                performLogoutNavigation()
            }
        }
    }

    private fun loadProfileData(uid: String) {
        // Carrega o nick
        repository.getUserNickByUid(uid,
            onSuccess = { nick ->
                textNick.text = "Apelido: $nick"
            },
            // 🌟 CORREÇÃO 1: Trata a falha de Nick (pode ter sido um erro de compilação na lambda)
            onFailure = { e: Exception ->
                textNick.text = "Apelido: Não encontrado."
                Log.e("Profile", "Falha ao carregar nick: ${e.message}")
            }
        )

        // Carrega a Bio
        repository.getUserProfile(uid,
            onSuccess = { profile ->
                // Preenche os campos da Bio
                inputName.setText(profile.name)
                inputCompany.setText(profile.company)
                inputDescription.setText(profile.description)
            },
            // 🌟 CORREÇÃO 2: Trata a falha de Perfil (pode ter sido um erro de compilação na lambda)
            onFailure = { e: Exception ->
                Log.e("Profile", "Falha ao carregar perfil: ${e.message}")
            }
        )
    }

    private fun saveProfileData(uid: String) {
        val name = inputName.text.toString().trim()
        val company = inputCompany.text.toString().trim()
        val description = inputDescription.text.toString().trim()

        repository.updateProfile(uid, name, company, description,
            onSuccess = {
                Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(context, "Falha ao salvar perfil: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun performLogoutNavigation() {
        val intent = Intent(activity, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        activity?.finish()
    }
}