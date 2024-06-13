package com.ufes.callguard.UI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ufes.callguard.R
import com.ufes.callguard.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ButtonConfig.setOnClickListener { view ->
            startActivity(Intent(this, ConfigurationActivity::class.java))
        }

        binding.ButtonBloquear.setOnClickListener { view ->
            startActivity(Intent(this, BlockActivity::class.java))
        }

        binding.ButtonHistorico.setOnClickListener { view ->
            startActivity(Intent(this, HistoricHomeActivity::class.java))
        }

        binding.ButtonComunidade.setOnClickListener { view ->
            startActivity(Intent(this, CommunityActivity::class.java))
        }

    }
}