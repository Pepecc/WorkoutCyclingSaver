package com.example.proyectodam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectodam.UserApp.Companion.prefs
import com.example.proyectodam.databinding.ActivityAnadirBiciBinding
import com.example.proyectodam.utils.SavingDialog
import com.example.proyectodam.utils.SavingImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AnadirBici : AppCompatActivity() {

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //ID USUARIO PREFS
    private var uid_user = prefs.getUserUid()

    //PANTALLA DE GUARDANDO
    private val saving = SavingDialog(this)

    //PANTALLA GUARDADO FOTO
    private val savinImg = SavingImage(this)

    //BINDNG:
    private lateinit var binding : ActivityAnadirBiciBinding

    //PERMISOS:
    private val REQUEST_GALLERY = 1001

    private var ruta : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAnadirBiciBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DEFINIR EL INPUT TYPE DE LOS CAMPOS NUMERICOS:
        binding.ETkmAddBici.setRawInputType(InputType.TYPE_CLASS_NUMBER)

        binding.BTaddbike.setOnClickListener{
            addBici()
        }

        binding.ETfechaCompra.setOnClickListener{
            showDatePickerDialog()
        }

        //BOTON ABRIR CAMARA/GALERIA:
        binding.BTfotobici.setOnClickListener {
            subirImagenBici()
        }

    }//override

    //DATE PICKER:
    private fun showDatePickerDialog(){
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year)}
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun subirImagenBici(){
        checkGalleryPermission()
    }

    private fun checkGalleryPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestGalleryPermission()
        }
        else{
            openGallery()
        }
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if(intent.resolveActivity(packageManager)!=null){
            startActivityForResult(intent, REQUEST_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == REQUEST_GALLERY){
            savinImg.startSavingImg()

            val imgFoto = data?.data
            val folder : StorageReference = FirebaseStorage.getInstance().getReference().child("user")
            val filename : StorageReference = folder.child("file" + imgFoto!!.lastPathSegment)
            filename.putFile(imgFoto).addOnSuccessListener {
                ruta = it.storage.name
                savinImg.isDimissImg()
                Toast.makeText(applicationContext, "Imagen subida con éxito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                savinImg.isDimissImg()
                Toast.makeText(applicationContext, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
       Toast.makeText(this, "Abriendo cámara", Toast.LENGTH_SHORT).show()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 0)
    }

    private fun requestGalleryPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //EL USUARIO YA HA RECHAZADO LOS PERMISOS
            Toast.makeText(this, "Permisos rechazados", Toast.LENGTH_SHORT).show()
        }else{
            //pedir permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 888)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 777){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera()
            }else{
                Toast.makeText(this, "Permisos rechazados por primera vez", Toast.LENGTH_SHORT).show()
            }
        }
        if(requestCode == 888){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery()
            }else{
                Toast.makeText(this, "Permisos rechazados por primera vez", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.ETfechaCompra.setText("$day/${month+1}/$year")
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
        binding.ETmarcaBici.setText("")
        binding.ETmodelBici.setText("")
        binding.ETtipoAddBici.setText("")
        binding.ETkmAddBici.setText("")
        binding.ETfechaCompra.setText("")
    }

    private fun addBici(){
        if(binding.ETmarcaBici.text.isNullOrBlank() || binding.ETmodelBici.text.isNullOrBlank() || binding.ETtipoAddBici.text.isNullOrBlank() ||
                binding.ETkmAddBici.text.isNullOrBlank() || binding.ETfechaCompra.text.isNullOrBlank()){
                showAlert("Error", "Ningún campo puede estar vacío")
        }else{
            saving.startSaving()
            val datosBici = hashMapOf(
                    "uid_user" to uid_user,
                    "marca_bici" to binding.ETmarcaBici.text.trim().toString(),
                    "modelo_bici" to binding.ETmodelBici.text.trim().toString(),
                    "tipo_bici" to binding.ETtipoAddBici.text.trim().toString(),
                    "km_total" to binding.ETkmAddBici.text.trim().toString().toInt(),
                    "fecha_compra" to binding.ETfechaCompra.text.trim().toString(),
                    "imagen" to ruta
            )
            db.collection("bicicletas")
                    .add(datosBici)
                    .addOnSuccessListener {
                        saving.isDimiss()
                        Toast.makeText(this, "Bicicleta añadida con éxito", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                        abrirMisBicis()
                    }
                    .addOnFailureListener{
                        saving.isDimiss()
                        showAlert("Error", "No se ha podido añadir la bicicleta")
                    }
        }
    }

    private fun abrirMisBicis(){
        val intent = Intent(this, Bicicletas::class.java)
        startActivity(intent)
    }
}