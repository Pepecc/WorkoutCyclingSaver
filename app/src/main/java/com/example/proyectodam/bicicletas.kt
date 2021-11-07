package com.example.proyectodam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.proyectodam.databinding.ActivityBicicletasBinding
import kotlinx.android.synthetic.main.activity_bicicletas.*
import com.example.proyectodam.UserApp.Companion.prefs
import com.example.proyectodam.utils.LoadingDialog

class bicicletas : AppCompatActivity(), (DatosRVbicis) -> Unit {

    //ID USUARIO PREFS
    private var uid_user = prefs.getUserUid()

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //INSTANCIA DE LA COLECCION BICICLETAS:
    private var dbikes = db.collection("bicicletas")

    private lateinit var binding : ActivityBicicletasBinding

    private val loading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityBicicletasBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        buscarDatos()

        binding.BTaddBikeHist.setOnClickListener{
            val intent = Intent(this, anadirBici::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MiEspacio::class.java))
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
        val datosBicis = arrayListOf<DatosRVbicis>()
        db.collection("bicicletas")
            .whereEqualTo("uid_user", uid_user)
            .get()
            .addOnSuccessListener {
                documents ->
                if(documents.isEmpty){
                    loading.isDimiss()
                    showAlert("Atención", "No tienes ninguna bicicleta. Añade la primera")
                }
                else {
                    for (document in documents) {
                        //Traer datos del documento
                        datosBicis.add(
                                DatosRVbicis(
                                        id = document.id,
                                        marca = document.get("marca_bici").toString(),
                                        modelo = document.get("modelo_bici").toString(),
                                        tipo = document.get("tipo_bici").toString(),
                                        odometro = document.get("km_total").toString().toInt(),
                                        fechacompra = document.get("fecha_compra").toString(),
                                        rutaImg = document.get("imagen").toString()
                                )
                        )
                        loading.isDimiss()
                        rvMisBicis.layoutManager = LinearLayoutManager(this)
                        val adapter = BiciAdapter(datosBicis, this)
                        //adapter.notifyDataSetChanged()
                        rvMisBicis.adapter = adapter
                    }//for
                }
            }.addOnFailureListener{
                    loading.isDimiss()
                    Toast.makeText(applicationContext, "Error" , Toast.LENGTH_SHORT).show()
       }
    }

    override fun invoke(bicis: DatosRVbicis){
        val intent = Intent(this, visor_bicicletas::class.java)
        intent.putExtra("id", bicis.id)
        intent.putExtra("marca", bicis.marca)
        intent.putExtra("modelo", bicis.modelo)
        intent.putExtra("tipo", bicis.tipo)
        intent.putExtra("odometro", bicis.odometro)
        intent.putExtra("fechacompra", bicis.fechacompra)
        intent.putExtra("imagen", bicis.rutaImg)
        startActivity(intent)
    }
}
