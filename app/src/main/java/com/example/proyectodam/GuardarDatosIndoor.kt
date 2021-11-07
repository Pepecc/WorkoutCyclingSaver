package com.example.proyectodam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectodam.UserApp.Companion.prefs
import com.example.proyectodam.databinding.ActivityGuardarDatosIndoorBinding
import com.example.proyectodam.utils.SavingDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GuardarDatosIndoor : AppCompatActivity() {

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //BINDING:
    private lateinit var binding: ActivityGuardarDatosIndoorBinding

    //ID USUARIO PREFS
    private var uid_user = prefs.getUserUid()

    //PANTALLA DE GUARDANDO
    private val saving = SavingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGuardarDatosIndoorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DEFINIR EL INPUT TYPE DE LOS CAMPOS NUMERICOS:
        binding.ETtotaltimeIndoor.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETcaloriasIndoor.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETpulsomaxIndoor.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETpulsomedIndoor.setRawInputType(InputType.TYPE_CLASS_NUMBER)

        binding.BTsaveIndoorTrain.setOnClickListener{
            guardarDatosIndoor()
        }

        binding.ETdateIndoor.setOnClickListener{
            showDatePickerDialog()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ChooseTraining::class.java))
    }

    private fun showDatePickerDialog(){
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year)}
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day:Int, month:Int, year:Int){
        binding.ETdateIndoor.setText("$day/${month+1}/$year")
    }

    fun showAlert(titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun limpiarCampos(){
        binding.ETtotaltimeIndoor.setText("")
        binding.ETpulsomedIndoor.setText("")
        binding.ETpulsomaxIndoor.setText("")
        binding.ETcaloriasIndoor.setText("")
        binding.ETnotasIndoor.setText("")
        binding.ETdateIndoor.setHint("Selecciona fecha")
    }

    fun guardarDatosIndoor(){
        var notas: String = ""
        if(binding.ETtotaltimeIndoor.text.isNullOrBlank() || binding.ETpulsomedIndoor.text.isNullOrBlank()
                || binding.ETpulsomaxIndoor.text.isNullOrBlank() || binding.ETcaloriasIndoor.text.isNullOrBlank()
                || binding.ETdateIndoor.text.isNullOrBlank()) {
            showAlert("Atención", "Ningún campo puede estar vacío")
        }else{
            saving.startSaving()
            //AL CAMPO NOTAS HAY QUE ASIGNARLE UN VALOR POR DEFECTO POR SI EL USUARIO NO ESCRIBE NADA

            if(!binding.ETnotasIndoor.text.isNullOrBlank()) notas = binding.ETnotasIndoor.text.toString()

            val datosIndoor = hashMapOf(
                "tipo_train" to "indoor",
                "uid_user" to uid_user,
                "total_time" to binding.ETtotaltimeIndoor.text.trim().toString().toInt(),
                "pulso_med" to binding.ETpulsomedIndoor.text.trim().toString().toInt(),
                "pulso_max" to binding.ETpulsomaxIndoor.text.trim().toString().toInt(),
                "calorias" to binding.ETcaloriasIndoor.text.trim().toString().toInt(),
                "fecha" to binding.ETdateIndoor.text.trim().toString(),
                "notas" to notas,
                    //CAMPOS OUTDOOR:
                "desnivel" to 1,
                "distancia" to 1f,
                "pend_max" to 1,
                "pend_med" to 1,
                "velo_max" to 1,
                "velo_med" to 1,
                "timesplitID" to "",
            )

            db.collection("entrenamientos") //antes entrenamientos_indoor
                    .add(datosIndoor)
                    //aqui poner una pantalla de carga mientras llama a firebase
                    .addOnSuccessListener { documentReference ->

                        val cadena = null

                        Log.d(cadena, "Documento guardado con id ${documentReference.id}")
                        saving.isDimiss()
                        //MOSTRAR UN TOAST COMO QUE SE HA GUARDADO BIEN
                        Toast.makeText(this, "Entrenamiento guardado con éxito", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                        abrirHistorial()
                    }
                    .addOnFailureListener{
                        showAlert("Error", "No se ha podido guardar el entrenamiento")
                    }
        }
    }

    private fun abrirHistorial() {
        val intent = Intent(this, Historial::class.java)
        startActivity(intent)
    }
}