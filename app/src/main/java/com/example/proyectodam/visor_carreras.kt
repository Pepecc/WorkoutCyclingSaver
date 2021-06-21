package com.example.proyectodam

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectodam.databinding.ActivityVisorCarrerasBinding
import com.example.proyectodam.utils.LoadingDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class visor_carreras : AppCompatActivity() {

    private lateinit var binding:ActivityVisorCarrerasBinding

    //GUARDAR EL ID DEL DOCUMENTO ACTUAL:
    private var idDoc : String= ""

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    private val loading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVisorCarrerasBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bundle = intent.extras
        idDoc = bundle?.getString("id").toString()
        val name = bundle?.getString("name")
        val dist = bundle?.getInt("dist")
        val desniv = bundle?.getInt("desniv")
        val date = bundle?.getString("date")
        val localiz = bundle?.getString("localiz")
        val imagen = bundle?.getString("imagen")

        binding.TVtitleCarreras.setText(name)
        binding.TVfechaCarrera.setText(date)
        binding.TVdistanceRace.setText("$dist KM")
        binding.TVdesniv.setText("$desniv M+")
        binding.TVlocaliz.setText(localiz.toString())

        //MOSTRAR LA IMAGEN:
        var storageRef = FirebaseStorage.getInstance().reference.child("user/$imagen")
        var localfile = File.createTempFile("tempImage", "jpg")
        loading.startLoading()
        storageRef.getFile(localfile).addOnSuccessListener {
            var bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.ImgPerfilCarrera.setImageBitmap(bitmap)
            loading.isDimiss()
        }.addOnFailureListener{
            loading.isDimiss()
           // Toast.makeText(this, "Error, no se pudo descargar la imagen", Toast.LENGTH_SHORT).show()
        }

        binding.BTborrarCarrera.setOnClickListener {
            showAlert()
        }

    }//onCreate




    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atención")
        builder.setMessage("¿Quieres eliminar esta carrera?")
        builder.setPositiveButton("Si", DialogInterface.OnClickListener{ dialogInterface, i -> borrarRegistro() })
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun borrarRegistro(){
        db.collection("carreras")
                .document(idDoc)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Carrera eliminada de tu colección", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error, no se pudo eliminar la carrera", Toast.LENGTH_SHORT).show()
                }
        var intent = Intent(this, carreras::class.java)
        startActivity(intent)
    }


}//Clase