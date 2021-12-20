package com.example.proyectodam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectodam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonCerrarTerms.setOnClickListener {
            cerrar()
        }
    }
    private fun cerrar(){
        this.finish()
    }
}