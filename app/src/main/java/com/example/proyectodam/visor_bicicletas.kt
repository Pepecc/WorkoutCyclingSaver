package com.example.proyectodam

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.databinding.ActivityVisorBicicletasBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import com.example.proyectodam.utils.LoadingDialog


class visor_bicicletas : AppCompatActivity() {
    private lateinit var binding : ActivityVisorBicicletasBinding

    //GUARDAR EL ID DEL DOCUMENTO ACTUAL:
    private var idDoc : String= ""

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    private var imgDel = ""

    private val loading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVisorBicicletasBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bundle = intent.extras
        idDoc = bundle?.getString("id").toString()
        val marca = bundle?.getString("marca")
        val modelo = bundle?.getString("modelo")
        val tipo = bundle?.getString("tipo")
        val odometro = bundle?.getInt("odometro")
        val fechacompra = bundle?.getString("fechacompra")
        val imagen = bundle?.getString("imagen")


        binding.TVtitleBicis.setText(marca + " " + modelo)
        binding.TVTipoBiciVisor.setText(tipo)
        binding.kmodometro.setText(odometro.toString())
        binding.TVfechaVisorID.setText(fechacompra)

        //MOSTRAR LA IMAGEN:
            var storageRef = FirebaseStorage.getInstance().reference.child("user/$imagen")
            imgDel = "user/" + imagen.toString()
            var localfile = File.createTempFile("tempImage", "jpg")
            loading.startLoading()
            storageRef.getFile(localfile).addOnSuccessListener {
                var bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.ImagenBici.setImageBitmap(bitmap)
                loading.isDimiss()
            }.addOnFailureListener {
                loading.isDimiss()
                // Toast.makeText(this, "Error, no se pudo descargar la imagen", Toast.LENGTH_SHORT).show()
            }


        binding.BTborrarBicicleta.setOnClickListener {
            showAlert()
        }

    }//onCreate

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, bicicletas::class.java))
    }

    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atención")
        builder.setMessage("¿Quieres quitar esta bicicleta?")
        builder.setPositiveButton("Si", DialogInterface.OnClickListener { dialogInterface, i -> borrarRegistro() })
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun borrarRegistro(){
        db.collection("bicicletas")
                .document(idDoc)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Bicicleta eliminada de tu colección", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error, no se pudo eliminar la bicicleta", Toast.LENGTH_SHORT).show()
                }
        //BORRAR LA IMAGEN:
        var storageRef = FirebaseStorage.getInstance().reference.child(imgDel)
        storageRef.delete()
        var intent = Intent(this, bicicletas::class.java)
        startActivity(intent)
    }


}//clase