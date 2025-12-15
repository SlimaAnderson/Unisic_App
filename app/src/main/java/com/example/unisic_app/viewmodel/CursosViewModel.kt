package com.example.unisic_app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unisic_app.data.model.ModuloCurso
import com.example.unisic_app.data.repository.FirebaseRepository
import com.google.firebase.firestore.ListenerRegistration
import android.util.Log

class CursosViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private var modulosListener: ListenerRegistration? = null

    private val _modulos = MutableLiveData<List<ModuloCurso>>()
    val modulos: LiveData<List<ModuloCurso>> = _modulos

    init {
        carregarModulos()
    }

    private fun carregarModulos() {
        //xplicitando o tipo 'List<ModuloCurso>' para o parâmetro lambda
        modulosListener = repository.getModulosRealtime { modulosCarregados: List<ModuloCurso> ->

            Log.d("CursosViewModel", "Módulos carregados: ${modulosCarregados.size}")
            _modulos.value = modulosCarregados
        }
    }

    override fun onCleared() {
        super.onCleared()
        //Remover o listener para evitar vazamento de memória
        modulosListener?.remove()
    }
}