package com.example.unisic_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController
// ❌ REMOVA AS IMPORTAÇÕES DO FIREBASE E DA AUTHACTIVITY AQUI

class MainActivity : AppCompatActivity() {

    // ❌ REMOVA: private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🌟 O CÓDIGO DE PROTEÇÃO DE ROTA FOI REMOVIDO

        setContentView(R.layout.activity_main)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        bottomNav.setupWithNavController(navController)
    }
}