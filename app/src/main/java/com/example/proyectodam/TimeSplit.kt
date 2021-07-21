package com.example.proyectodam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectodam.databinding.ActivityTimeSplitBinding
import com.example.proyectodam.UserApp.Companion.prefs
import com.example.proyectodam.utils.Global
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TimeSplit : AppCompatActivity(){

    //ID USUARIO PREFS
    private var uid_user = prefs.getUserUid()

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //BINDING:
    private lateinit var binding: ActivityTimeSplitBinding

    //VARIABLE GLOBAL EN LA QUE GUARDO EL ID DEL ENTRENAMIENTO QUE
    //LE LLEGA POR INTENT
    private var key : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTimeSplitBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ETtramoTS.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.ETminutesTS.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETsecondsTS.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETdistTS.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETnotesTS.setRawInputType(InputType.TYPE_CLASS_TEXT)

        //RECIBE EL IDENTIFICADOR DEL ENTRENAMIENTO AL QUE IRÁ ASOCIADO CADA TS
        val bundle = intent.extras
        val clave = bundle?.getString("clave").toString()

        //GUARDAR EL ID EN UNA VARIABLE GLOBAL
        key = clave

        //BOTÓN NUEVA VUELTA
        binding.BTnuevaLap.setOnClickListener {
            guardarArray()
        }

        //BOTÓN GUARDAR SOLO UNA VUELTA
        binding.BTsaveTS.setOnClickListener {
            almacenarDatos()
        }
    }//override

    fun showAlert(titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun mostrarToast(){
        Toast.makeText(this, "Registro time split añadido con éxito", Toast.LENGTH_SHORT).show()
    }

    fun limpiarCampos(){
        binding.ETtramoTS.setText("")
        binding.ETminutesTS.setText("")
        binding.ETsecondsTS.setText("")
        binding.ETdistTS.setText("")
        binding.ETnotesTS.setText("")
    }

    //GUARDAR LOS DATOS EN LA LISTA SI EL USUARIO SÓLO GUARDA UNA VUELTA TS
    fun almacenarDatos() {
        var notas: String = ""
        if (binding.ETtramoTS.text.isNullOrBlank() || binding.ETminutesTS.text.isNullOrBlank() || binding.ETsecondsTS.text.isNullOrBlank() ||
                binding.ETdistTS.text.isNullOrBlank()) {
            showAlert("Error", "Ningún campo puede estar vacío")
        } else {
            //SI EL CAMPO NOTAS NO TIENE VALOR LE ASIGNAMOS UNO POR DEFECTO:
            if (!binding.ETnotesTS.text.isNullOrBlank()) notas = binding.ETnotesTS.text.toString()

           val datosTimeSplit = hashMapOf(
                            "idUser" to uid_user,
                            "idSesion" to key,
                            "tramo" to binding.ETtramoTS.text.toString(),
                            "minutos" to binding.ETminutesTS.text.toString().toInt(),
                            "segundos" to binding.ETsecondsTS.text.toString().toInt(),
                            "dist" to binding.ETdistTS.text.toString().toFloat(),
                            "notas" to notas
                    )
            db.collection("timesplit")
                    .add(datosTimeSplit)
                    .addOnSuccessListener {
                        Toast.makeText(this, "TS guardada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        showAlert("Error", "No se ha podido guardar TS")
                    }

            Global.idSesionTrain = key
            //CREAR INTENT:
            val intent = Intent(this, GuardarDatosOutdoor::class.java)
            mostrarToast()
            //VOLVER A ACTIVITY GUARDAR DATOS OUTDOOR:
            startActivity(intent)
        }
    }

    //GUARDAR LOS REGISTROS EN LISTAS SI EL USUARIO GUARDA MÁS DE UNA VUELTA TS
    fun guardarArray(){
        var notas : String = ""
        if(binding.ETtramoTS.text.isNullOrBlank() && binding.ETminutesTS.text.isNullOrBlank() && binding.ETsecondsTS.text.isNullOrBlank() &&
                binding.ETdistTS.text.isNullOrBlank())    {
                showAlert("Error", "Ningún campo puede estar vacío")
        }else{
            //SI EL CAMPO NOTAS NO TIENE VALOR LE ASIGNAMOS UNO POR DEFECTO:
            if(!binding.ETnotesTS.text.isNullOrBlank()) notas = binding.ETnotesTS.text.toString()

            //GUARDAR LOS REGISTROS EN EL ARRAY DE LISTAS
            val datosTimeSplit = hashMapOf(
                    "idUser" to uid_user,
                    "idSesion" to key,
                    "tramo" to binding.ETtramoTS.text.toString(),
                    "minutos" to binding.ETminutesTS.text.toString().toInt(),
                    "segundos" to binding.ETsecondsTS.text.toString().toInt(),
                    "dist" to binding.ETdistTS.text.toString().toFloat(),
                    "notas" to notas
            )
            db.collection("timesplit")
                    .add(datosTimeSplit)
                    .addOnSuccessListener {
                        Toast.makeText(this, "TS guardada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        showAlert("Error", "No se ha podido guardar TS")
                    }
            Global.idSesionTrain = key
            //CONFIRMACIÓN DE QUE SE HAN GUARDADO CON ÉXITO EN EL ARRAY:
            mostrarToast()
            //LIMPIAR LOS CAMPOS:
            limpiarCampos()
        }
    }//funcion

}//clase