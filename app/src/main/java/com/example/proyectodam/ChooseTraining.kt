package com.example.proyectodam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectodam.databinding.ActivityChooseTrainingBinding
import android.content.Intent

class ChooseTraining : AppCompatActivity() {
    private lateinit var binding: ActivityChooseTrainingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
       binding = ActivityChooseTrainingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.CVoutdoor.setOnClickListener{
            abrirOutdoorTrain()
        }

        binding.CVindoor.setOnClickListener{
            abrirIndoorTrain()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MenuInicial::class.java))
    }

    private fun abrirOutdoorTrain(){
        val intent = Intent(this, GuardarDatosOutdoor::class.java)
        startActivity(intent)
    }

    private  fun abrirIndoorTrain(){
        val intent = Intent(this, GuardarDatosIndoor::class.java)
        startActivity(intent)
    }
}