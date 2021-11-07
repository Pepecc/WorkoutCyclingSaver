package com.example.proyectodam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectodam.databinding.ActivityCarrerasBinding
import com.example.proyectodam.utils.LoadingDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_carreras.*

class carreras : AppCompatActivity(), (DatosRVraces) -> Unit {

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //ID USUARIO PREFS
    private var uid_user = UserApp.prefs.getUserUid()

    //IMPORTAR FUNCIÓN DE CARGA
    private val loading = LoadingDialog(this)

    private lateinit var binding: ActivityCarrerasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCarrerasBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        buscarDatos()

        binding.BTaddRaceBtn.setOnClickListener{
            addRaces()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MiEspacio::class.java))
    }

    fun addRaces(){
        val intent = Intent(this, anadir_carrera::class.java)
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

    fun buscarDatos(){
        loading.startLoading()
        val datosRaces = arrayListOf<DatosRVraces>()
        db.collection("carreras")
            .whereEqualTo("uid_user", uid_user)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    loading.isDimiss()
                    showAlert("Atención", "No tienes carreras guardadas, prueba a guardar la primera")
                }
                for (document in result) {
                    datosRaces.add(
                        DatosRVraces(
                                id = document.id,
                                name = document.get("name_race").toString(),
                                dist = document.get("distance_race").toString().toInt(),
                                desniv = document.get("desnivel_race").toString().toInt(),
                                date = document.get("date_race").toString(),
                                localiz = document.get("localizacion").toString(),
                                rutaImg = document.get("imagen").toString()
                        ),
                    )
                    loading.isDimiss()
                    rvMisRaces.layoutManager = LinearLayoutManager(this)
                    val adapter = RaceAdapter(datosRaces, this)
                    rvMisRaces.adapter = adapter
                }

            }//succesListener
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }

    }

    override fun invoke(races: DatosRVraces) {
        val intent = Intent(this, visor_carreras::class.java)
        intent.putExtra("id", races.id)
        intent.putExtra("name", races.name)
        intent.putExtra("dist", races.dist)
        intent.putExtra("desniv", races.desniv)
        intent.putExtra("date", races.date)
        intent.putExtra("localiz", races.localiz)
        intent.putExtra("imagen", races.rutaImg)
        startActivity(intent)
    }
}