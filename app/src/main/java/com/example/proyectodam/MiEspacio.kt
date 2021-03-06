package com.example.proyectodam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectodam.databinding.ActivityMiEspacioBinding

class MiEspacio : AppCompatActivity() {
    private lateinit var binding: ActivityMiEspacioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMiEspacioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.CVmibici.setOnClickListener{
            abrirBicis()
        }

        binding.CVmisMarchas.setOnClickListener{
            abrirCarreras()
        }

        binding.CVmisTrains.setOnClickListener {
            abrirTrainings()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MenuInicial::class.java))
    }

    private fun abrirTrainings(){
        val intent = Intent(this, Historial::class.java)
        startActivity(intent)
    }

    private fun abrirBicis(){
        val intent = Intent(this, Bicicletas::class.java)
        startActivity(intent)
    }

    private fun abrirCarreras(){
        val intent = Intent(this, Carreras::class.java)
        startActivity(intent)
    }
}