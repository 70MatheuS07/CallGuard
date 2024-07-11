package com.ufes.callguard.UI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.ufes.callguard.R
import com.ufes.callguard.databinding.ActivityHomeBinding

/**
 * Tela inicial do aplicativo
 */
class  HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        // Direciona para a tela de configuração
        binding.ButtonConfig.setOnClickListener { view ->
            startActivity(Intent(this, ConfigurationActivity::class.java))
        }
        // Direciona para a tela de bloqueio
        binding.ButtonBloquear.setOnClickListener { view ->
            startActivity(Intent(this, BlockActivity::class.java))
        }
        // Direciona para a tela de histórico
        binding.ButtonHistorico.setOnClickListener { view ->
            startActivity(Intent(this, HistoricHomeActivity::class.java))
        }
        // Direciona para a tela da comunidade
        binding.ButtonComunidade.setOnClickListener { view ->
            startActivity(Intent(this, CommunityActivity::class.java))
        }

    }
}