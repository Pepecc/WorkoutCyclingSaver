package com.example.proyectodam

import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectodam.databinding.ActivityHistorialBinding
import kotlinx.android.synthetic.main.activity_historial.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.proyectodam.utils.LoadingDialog
import com.google.firebase.firestore.Query

class Historial : AppCompatActivity(), (DatosRVoutdoor) -> Unit {

    //BINDING:
    private lateinit var binding: ActivityHistorialBinding

    //ID USUARIO PREFS
    private var uid_user = UserApp.prefs.getUserUid()

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore.collection("entrenamientos")

    //PANTALLA DE CARGA:
    private val loading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityHistorialBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        buscarDatos()

        binding.BTaddTrainHistorial.setOnClickListener{
            abrirGuardarDatos()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MiEspacio::class.java))
    }

    fun abrirGuardarDatos(){
        val intent =  Intent(this, ChooseTraining::class.java)
        startActivity(intent)
    }

    fun showAlert(titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    fun buscarDatos() {
        loading.startLoading()
       var datosOutdoorTrain = arrayListOf<DatosRVoutdoor>()
        //CARGAR DATOS OUTDOOR:
               db.whereEqualTo("uid_user", uid_user)
                       //.orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        loading.isDimiss()
                        showAlert("Atención", "No tienes entrenamientos guardados, prueba a guardar el primero")
                    }
                        for (document in documents) {
                            datosOutdoorTrain.add(
                                    DatosRVoutdoor(
                                            id = document.id,
                                            tipo = document.get("tipo_train").toString(),
                                            fecha = document.get("fecha").toString(),
                                            tiempo = document.get("total_time").toString().toInt(),
                                            calorias = document.get("calorias").toString().toInt(),
                                            km = document.get("distancia").toString().toFloat(),
                                            pulso_med = document.get("pulso_med").toString().toInt(),
                                            pulso_max = document.get("pulso_max").toString().toInt(),
                                            pend_med = document.get("pend_med").toString().toInt(),
                                            pend_max = document.get("pend_max").toString().toInt(),
                                            notas = document.get("notas").toString(),
                                            vmedia = document.get("velo_med").toString().toInt(),
                                            vmax = document.get("velo_max").toString().toInt(),
                                            desnivelAcum = document.get("desnivel").toString().toInt(),
                                            idTs = document.get("timesplitID").toString()
                                    )
                            )
                            loading.isDimiss()
                            rvDatosTotales.layoutManager = LinearLayoutManager(this)
                            val adapter2 = DataAdapter(datosOutdoorTrain, this)
                            rvDatosTotales.adapter = adapter2
                         //   adapter2.notifyDataSetChanged()
                    }//for
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    }
    }//funcion

    override fun invoke(datos: DatosRVoutdoor) {
        if(datos.tipo == "indoor"){
            val intent = Intent(this, visorTrainIndoor::class.java)
            intent.putExtra("id", datos.id)
            intent.putExtra("fecha", datos.fecha)
            intent.putExtra("calorias", datos.calorias)
            intent.putExtra("tiempo", datos.tiempo)
            intent.putExtra("pulsmax", datos.pulso_max)
            intent.putExtra("pulsmed", datos.pulso_med)
            intent.putExtra("notas", datos.notas)
            startActivity(intent)
        }else{
            val intent = Intent(this, visor_train_outdoor::class.java)
            intent.putExtra("id", datos.id)
            intent.putExtra("fecha", datos.fecha)
            intent.putExtra("calorias", datos.calorias)
            intent.putExtra("tiempo", datos.tiempo)
            intent.putExtra("pulsmax", datos.pulso_max)
            intent.putExtra("pulsmed", datos.pulso_med)
            intent.putExtra("notas", datos.notas)
            intent.putExtra("distancia", datos.km)
            intent.putExtra("vmed", datos.vmedia)
            intent.putExtra("vmax", datos.vmax)
            intent.putExtra("desnivel", datos.desnivelAcum)
            intent.putExtra("pendmed", datos.pend_med)
            intent.putExtra("pendmax", datos.pend_max)
            intent.putExtra("idTs", datos.idTs)
            startActivity(intent)
        }
    }
}


