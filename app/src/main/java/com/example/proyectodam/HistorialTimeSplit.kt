package com.example.proyectodam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectodam.databinding.ActivityHistorialTimeSplitBinding
import com.example.proyectodam.utils.LoadingDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_historial_time_split.*

class HistorialTimeSplit : AppCompatActivity(), (DatosRVtimeSplit) -> Unit {

    //BINDING:
    private lateinit var binding: ActivityHistorialTimeSplitBinding

    //PANTALLA DE CARGA:
    private val loading = LoadingDialog(this)

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //ID USUARIO PREFS
    private var uid_user = UserApp.prefs.getUserUid()

    //VARIABLE GLOBAL PARA ALMACENAR EL ID:
    private var id : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHistorialTimeSplitBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bundle = intent.extras
        //val idTs : String? = bundle?.getString("idTS")
        id = bundle?.getString("idTS").toString()
        buscarDatos()
    }

    fun showAlert(titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun buscarDatos() {
        loading.startLoading()
        val registrosTimeSplit = arrayListOf<DatosRVtimeSplit>()
        //CARGAR DATOS DE FIREBASE:
        db.collection("timesplit")
            .whereEqualTo("idSesion", id)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    loading.isDimiss()
                    showAlert("Atención", "No tienes registros de TS")
                }
                for (document in documents) {
                    registrosTimeSplit.add(
                        DatosRVtimeSplit(
                            dist = document.get("dist").toString().toFloat(),
                            idSesion = document.get("idSesion").toString(),
                            idUser = document.get("idUser").toString(),
                            minutos = document.get("minutos").toString().toInt(),
                            segundos = document.get("segundos").toString().toInt(),
                            notas = document.get("notas").toString(),
                            tramo = document.get("tramo").toString()
                        )
                    )
                    loading.isDimiss()
                    rvDatosTotalesTS.layoutManager = LinearLayoutManager(this)
                    val adapterTS = DatosTSadapter(registrosTimeSplit, this)
                    rvDatosTotalesTS.adapter = adapterTS
                }//for
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    override fun invoke(ts: DatosRVtimeSplit) {

    }

}