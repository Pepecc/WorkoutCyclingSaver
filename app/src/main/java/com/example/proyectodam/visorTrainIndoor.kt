package com.example.proyectodam

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectodam.databinding.ActivityVisorTrainIndoorBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class visorTrainIndoor : AppCompatActivity() {
    private lateinit var binding: ActivityVisorTrainIndoorBinding

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    private var idDoc : String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVisorTrainIndoorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.BTborrarTrainID.setOnClickListener {
            showAlert()
        }

        val bundle = intent.extras
        val fecha : String? = bundle?.getString("fecha")
        val calorias :Int? = bundle?.getInt("calorias")
        val tiempo : Int? = bundle?.getInt("tiempo")
        val pulsmax : Int? = bundle?.getInt("pulsmax")
        val pulsmed : Int? = bundle?.getInt("pulsmed")
        val notas : String? = bundle?.getString("notas")
        val id = bundle?.getString("id")
        if (id != null) {
            idDoc = id
        }

        binding.TVcalorVisorID.setText(" ${calorias.toString()} kcal")
        binding.TVfechaVisorID.setText(fecha.toString())
        binding.TVduracVisorID.setText(" ${tiempo.toString()} minutos")
        binding.TVpulsMedvisorID.setText(" ${pulsmed.toString()} ppm")
        binding.TVpulsMaxvisorID.setText(" ${pulsmax.toString()} ppm")
        binding.TVnotesID.setText(notas)
    }

    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atención")
        builder.setMessage("¿Quieres eliminar este entrenamiento?")
        builder.setPositiveButton("Si", DialogInterface.OnClickListener{dialogInterface, i -> borrarRegistro() })
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, Historial::class.java))
    }

    private fun borrarRegistro(){
        db.collection("entrenamientos")
                .document(idDoc)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Entrenamiento eliminado con éxito", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error, no se pudo eliminar el entrenamiento", Toast.LENGTH_SHORT).show()
                }
        val intent = Intent(this, Historial::class.java)
        startActivity(intent)
    }
}