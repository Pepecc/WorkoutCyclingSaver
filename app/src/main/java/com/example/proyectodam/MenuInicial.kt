package com.example.proyectodam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.databinding.ActivityMenuInicialBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.proyectodam.UserApp.Companion.prefs

enum class ProviderType{
    BASIC
}

class MenuInicial : AppCompatActivity() {
    private lateinit var binding: ActivityMenuInicialBinding

    //RECUPERAR EL UID DEL USUARIO ACTUAL:
    private var user = FirebaseAuth.getInstance().currentUser.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMenuInicialBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        guardarUid()

        binding.CVguardar.setOnClickListener{
            abrirMenuTrain()
        }

        binding.CVstats.setOnClickListener{
            abrirStats()
        }

        binding.CVmiEspacio.setOnClickListener{
            abrirMiEspacio()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ActivityInicial::class.java))
        Toast.makeText(applicationContext, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }

    private fun guardarUid(){
        prefs.saveUserUid(user)
    }

    private fun abrirMenuTrain(){
        val intent = Intent(this, ChooseTraining::class.java)
        startActivity(intent)
    }

    private fun abrirStats(){
        //val intent = Intent(this, Historial::class.java)
        val intent = Intent(this, Estadisticas::class.java)
        startActivity(intent)
    }

    private fun abrirMiEspacio(){
        val intent = Intent(this, MiEspacio::class.java)
        startActivity(intent)
    }
}