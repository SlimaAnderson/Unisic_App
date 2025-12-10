package com.example.unisic_app // Use o nome do seu package principal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.unisic_app.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Encontra o NavHost (onde os Fragments serão exibidos)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 2. Encontra a BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        // 3. Conecta a BottomNavigationView ao NavController
        bottomNav.setupWithNavController(navController)
    }
}