package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unisic_app.R
import com.example.unisic_app.data.model.Postagem
import com.example.unisic_app.data.repository.FirebaseRepository // Necessita de um Repository (assumindo que já existe)
import com.google.firebase.auth.FirebaseAuth // Necessita para obter o autor
import java.text.SimpleDateFormat
import java.util.*

class NovaPostagemFragment : Fragment(R.layout.fragment_nova_postagem) {

    private val repository = FirebaseRepository()


    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnPublish: Button
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.edit_text_title)
        etContent = view.findViewById(R.id.edit_text_content)
        btnPublish = view.findViewById(R.id.button_publish)
        progressBar = view.findViewById(R.id.progress_bar)

        btnPublish.setOnClickListener {
            publishPost()
        }
    }

    private fun publishPost() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(context, "Título e conteúdo não podem estar vazios.", Toast.LENGTH_SHORT).show()
            return
        }



        // Criar o objeto Postagem (Garantindo que comentários é uma lista vazia)
        val newPost = Postagem(
            titulo = title,
            texto = content,
            comentarios = emptyList()
        )

        progressBar.visibility = View.VISIBLE
        btnPublish.isEnabled = false

        // Salvar no Firestore
        repository.addPostagem(newPost,
            onSuccess = {
                Toast.makeText(context, "Postagem publicada!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                // Voltar para a tela anterior (ComunidadeFragment)
                findNavController().popBackStack()
            },
            onFailure = { error ->
                Toast.makeText(context, "Falha ao publicar: ${error.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                btnPublish.isEnabled = true
            }
        )
    }
}