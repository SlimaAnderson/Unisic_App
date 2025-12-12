package com.example.unisic_app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unisic_app.data.model.Postagem
import com.example.unisic_app.data.repository.FirebaseRepository
import com.google.firebase.firestore.ListenerRegistration

class ForumViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    // LiveData que o Fragment observará
    private val _postagens = MutableLiveData<List<Postagem>>()
    val postagens: LiveData<List<Postagem>> = _postagens

    // Listener do Firestore para atualizações em tempo real
    private var firestoreListener: ListenerRegistration? = null

    init {
        carregarPostagens()
    }

    /**
     * Inicia a escuta (listener) de posts do Firestore.
     * Sempre que um post é adicionado, modificado ou removido, o LiveData é atualizado.
     */
    private fun carregarPostagens() {
        firestoreListener = repository.getPostsRealtime { posts ->
            _postagens.value = posts
        }
    }

    // 🌟 Importante: Fechar o listener quando o ViewModel for destruído.
    override fun onCleared() {
        super.onCleared()
        firestoreListener?.remove()
    }
}