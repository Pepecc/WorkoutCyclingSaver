package com.example.proyectodam

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectodam.databinding.ActivityVisorTrainOutdoorBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class visor_train_outdoor : AppCompatActivity() {
    private lateinit var binding: ActivityVisorTrainOutdoorBinding

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //INSTANCIA DE LA COLECCION TS:
    private var dbTS = Firebase.firestore.collection("timesplit")

    //GUARDAR EL ID DEL DOCUMENTO ACTUAL:
    private var idDoc : String= ""

    //GUARDAR ID DEL DOCUMENTO TS ACTUAL:
    private var idTSdoc : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVisorTrainOutdoorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bundle = intent.extras
        val fecha : String? = bundle?.getString("fecha")
        val calorias : Int? = bundle?.getInt("calorias")
        val tiempo : Int? = bundle?.getInt("tiempo")
        val pulsmax : Int? = bundle?.getInt("pulsmax")
        val pulsmed : Int? = bundle?.getInt("pulsmed")
        val notas : String? = bundle?.getString("notas")
        val distancia : Float? = bundle?.getFloat("distancia")
        val vmedia : Int? = bundle?.getInt("vmed")
        val vmax :Int? = bundle?.getInt("vmax")
        val desnivel : Int? = bundle?.getInt("desnivel")
        val pendmed : Int? = bundle?.getInt("pendmed")
        val pendmax : Int? = bundle?.getInt("pendmax")
        val idTs : String? = bundle?.getString("idTs")
        val id = bundle?.getString("id")
        if (id != null) {
            idDoc = id
        }

        idTSdoc = idTs.toString()

        binding.BTborrarTrainOD.setOnClickListener {
            showAlert()
        }

        binding.BTverTS.setOnClickListener {
            val intent = Intent(this, HistorialTimeSplit :: class.java)
            intent.putExtra("idTS", idTs)
            startActivity(intent)
        }

        dbTS.whereEqualTo("idSesion", idTs)
            .get()
            .addOnSuccessListener {
                    documents ->
                if(documents.isEmpty){
                    //Toast.makeText(applicationContext, "No hay TS", Toast.LENGTH_SHORT).show()
                    binding.BTverTS.visibility = View.INVISIBLE
                }
                else{
                    //Toast.makeText(applicationContext, "Si hay TS", Toast.LENGTH_SHORT).show()
                    binding.BTverTS.visibility = View.VISIBLE

                }
            }

        binding.TVfechaVisorOD.setText(fecha.toString())
        binding.TVpulsMedvisorOD.setText("${pulsmed.toString()} ppm")
        binding.TVpulsMaxvisorID.setText("${pulsmax.toString()} ppm")
        binding.TVduracVisorOD.setText( "${tiempo.toString()} minutos")
        binding.TVdistancVisorOD.setText("${distancia.toString()} kilómetros")
        binding.TVvelomedVisorOD.setText("${vmedia.toString()} km/h")
        binding.TVvelomaxVisorOD.setText("${vmax.toString()} km/h")
        binding.TVdesnivacumVisorOD.setText("${desnivel.toString()} metros")
        binding.TVcaloriesVisorOD.setText("${calorias.toString()} kcal")
        binding.TVdesnivmedVisorOD.setText("${pendmed.toString()} %")
        binding.TVpendmaxVisorOD.setText("${pendmax.toString()} %")
        binding.TVnotasVisorODTitle.setText(notas.toString())

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, Historial::class.java))
    }

    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atención")
        builder.setMessage("¿Quieres eliminar este entrenamiento?")
        builder.setPositiveButton("Si", DialogInterface.OnClickListener{ dialogInterface, i -> borrarRegistro() })
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun borrarRegistro(){
            db.collection("entrenamientos")
                    .document(idDoc)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Entrenamiento eliminado con éxito", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error, no se pudo eliminar el entrenamiento", Toast.LENGTH_SHORT).show()
                    }
            db.collection("timesplit")
                .document()

            var intent = Intent(this, Historial::class.java)
            startActivity(intent)
    }
}