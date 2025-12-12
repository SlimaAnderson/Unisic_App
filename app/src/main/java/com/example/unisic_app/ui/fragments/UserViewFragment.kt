package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unisic_app.R
import com.example.unisic_app.data.repository.FirebaseRepository

class UserViewFragment : Fragment(R.layout.fragment_user_view) {

    private val repository = FirebaseRepository()

    private lateinit var textViewNick: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewCompany: TextView
    private lateinit var textViewDescription: TextView

    private var targetUid: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar Views
        textViewNick = view.findViewById(R.id.text_view_nick)
        textViewName = view.findViewById(R.id.text_view_name)
        textViewCompany = view.findViewById(R.id.text_view_company)
        textViewDescription = view.findViewById(R.id.text_view_description)

        // 2. Obter o UID do Bundle
        targetUid = arguments?.getString("profileUid")

        if (targetUid.isNullOrEmpty()) {
            Toast.makeText(context, "Erro: UID do perfil ausente.", Toast.LENGTH_LONG).show()
            return
        }

        // 3. Carregar e Exibir Dados
        loadProfileData(targetUid!!)
    }

    private fun loadProfileData(uid: String) {
        // Carrega o Nick (Apelido)
        repository.getUserNickByUid(uid,
            onSuccess = { nick ->
                textViewNick.text = "Apelido: $nick"
            },
            onFailure = { e ->
                textViewNick.text = "Apelido: Não encontrado"
                Log.e("UserView", "Falha ao carregar Nick: ${e.message}")
            }
        )

        // Carrega o Perfil (Bio, Nome, Empresa)
        repository.getUserProfile(uid,
            onSuccess = { profile ->
                // Preenche os campos de visualização
                textViewName.text = if (profile.name.isNotEmpty()) profile.name else "Não informado"
                textViewCompany.text = if (profile.company.isNotEmpty()) profile.company else "Não informado"
                textViewDescription.text = if (profile.description.isNotEmpty()) profile.description else "Nenhuma Bio adicionada."
            },
            onFailure = { e ->
                Toast.makeText(context, "Falha ao carregar Bio.", Toast.LENGTH_SHORT).show()
                Log.e("UserView", "Falha ao carregar Perfil: ${e.message}")
            }
        )
    }
}