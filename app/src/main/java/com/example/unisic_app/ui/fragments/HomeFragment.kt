package com.example.unisic_app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.unisic_app.R // Importe R.kt do seu namespace

// O construtor primário (R.layout.fragment_home) garante que o layout seja inflado.
class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Lógica para carregar Notícias e o logo virá aqui
    }
}