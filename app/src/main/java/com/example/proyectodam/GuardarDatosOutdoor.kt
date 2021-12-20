package com.example.proyectodam

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.UserApp.Companion.prefs
import com.example.proyectodam.databinding.ActivityGuardarDatosOutdoorBinding
import com.example.proyectodam.utils.Global
import com.example.proyectodam.utils.SavingDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GuardarDatosOutdoor : AppCompatActivity() {

    private lateinit var binding: ActivityGuardarDatosOutdoorBinding

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //RECUPERAR ID USER DE SHARED PREFS:
    private var id_user = prefs.getUserUid()

    //PANTALLA DE GUARDANDO
    private val saving = SavingDialog(this)

    private val idTS = System.currentTimeMillis().toString()

    private var indice : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGuardarDatosOutdoorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DEFINIR EL INPUT TYPE DE LOS CAMPOS NUMERICOS:
        binding.ETtotaltimeOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETdistOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETpuslomedOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETpuslomaxOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETcaloriasOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETvmediaOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETvmaxOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETpendMediaOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETpendMaxOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETdesnivelOd.setRawInputType(InputType.TYPE_CLASS_NUMBER)

        binding.ETfechaOd.setOnClickListener{
            showDatePickerDialog()
        }

        indice = idTS

        binding.BTtimesplit.setOnClickListener{
            val intent = Intent(this, TimeSplit::class.java)
            intent.putExtra("clave", indice)
            startActivity(intent)
        }

        binding.BTsaveOutdoorTrain.setOnClickListener{
            guardarDatosOutdoor()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ChooseTraining::class.java))
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year)}
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int){
        binding.ETfechaOd.setText("$day/${month+1}/$year")
    }

    fun showAlert(titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun limpiarCampos(){
        //CAMPOS OBLIGATORIOS:
        binding.ETfechaOd.setText("  Selecciona Fecha")
        binding.ETtotaltimeOd.setText("")
        binding.ETdistOd.setText("")
        binding.ETpuslomaxOd.setText("")
        binding.ETpuslomedOd.setText("")
        binding.ETcaloriasOd.setText("")
        binding.ETvmediaOd.setText("")
        binding.ETvmaxOd.setText("")
        binding.ETpendMediaOd.setText("")
        binding.ETpendMaxOd.setText("")
        binding.ETdesnivelOd.setText("")
        binding.ETnotasODtrainNew.setText("")
    }

    private fun abrirHistorial() {
        val intent = Intent(this, Historial::class.java)
        startActivity(intent)
    }

    private fun guardarDatosOutdoor(){
        var notas: String = ""
        var idDoc: String = ""
        if(binding.ETtotaltimeOd.text.isNullOrBlank() || binding.ETdistOd.text.isNullOrBlank() || binding.ETpuslomedOd.text.isNullOrBlank()
                || binding.ETpuslomaxOd.text.isNullOrBlank()|| binding.ETcaloriasOd.text.isNullOrBlank()|| binding.ETvmediaOd.text.isNullOrBlank()
                || binding.ETvmaxOd.text.isNullOrBlank()|| binding.ETpendMediaOd.text.isNullOrBlank()|| binding.ETvmaxOd.text.isNullOrBlank()
                || binding.ETdesnivelOd.text.isNullOrBlank()){
                showAlert("Atención", "Todos los campos son obligatorios")
        }else{
            //INICIAR SPINER DE CARGA:
            saving.startSaving()
            if(!binding.ETnotasODtrainNew.text.isNullOrBlank()) notas = binding.ETnotasODtrainNew.text.toString()
                val datosOutDoor = hashMapOf(
                        //CAMPOS OBLIGATORIOS:
                        "uid_user" to id_user,
                        "tipo_train" to "outdoor",
                        "total_time" to binding.ETtotaltimeOd.text.trim().toString().toInt(),
                        "distancia" to binding.ETdistOd.text.trim().toString().toFloat(),
                        "calorias" to binding.ETcaloriasOd.text.trim().toString().toInt(),
                        "pulso_med" to binding.ETpuslomedOd.text.trim().toString().toInt(),
                        "pulso_max" to binding.ETpuslomaxOd.text.trim().toString().toInt(),
                        "fecha" to binding.ETfechaOd.text.trim().toString(),
                        "velo_med" to binding.ETvmediaOd.text.trim().toString().toInt(),
                        "velo_max" to binding.ETvmaxOd.text.trim().toString().toInt(),
                        "pend_med" to binding.ETpendMediaOd.text.trim().toString().toInt(),
                        "pend_max" to binding.ETpendMaxOd.text.trim().toString().toInt(),
                        "timesplitID" to Global.idSesionTrain,
                        "desnivel" to binding.ETdesnivelOd.text.trim().toString().toInt(),
                        "notas" to notas
                )
                db.collection("entrenamientos")
                        .add(datosOutDoor)
                        .addOnSuccessListener {
                            idDoc = it.id
                            saving.isDimiss()
                            Toast.makeText(this, "Entrenamiento guardado con éxito", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                            abrirHistorial()
                        }
                        .addOnFailureListener {
                            showAlert("Error", "No se ha podido guardar el entrenamiento")
                        }
            Global.idSesionTrain=""
        }
    }
}