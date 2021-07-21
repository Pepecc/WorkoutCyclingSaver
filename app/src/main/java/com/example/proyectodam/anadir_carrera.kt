package com.example.proyectodam

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectodam.utils.SavingImage
import com.example.proyectodam.utils.SavingDialog
import androidx.core.content.ContextCompat
import com.example.proyectodam.databinding.ActivityAnadirCarreraBinding
import com.example.proyectodam.UserApp.Companion.prefs
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.provider.MediaStore
import androidx.core.app.ActivityCompat

class anadir_carrera : AppCompatActivity() {

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //ID USUARIO PREFS
    private var uid_user = UserApp.prefs.getUserUid()

    //PERMISOS:
    private val REQUEST_GALLERY = 1001
    private val REQUEST_CAMERA = 1002

    //PANTALLA GUARDADO FOTO
    private val savinImg = SavingImage(this)

    //PANTALLA DE GUARDANDO
    private val saving = SavingDialog(this)

    private var ruta : String = ""

    private lateinit var binding : ActivityAnadirCarreraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAnadirCarreraBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DEFINIR EL INPUT DE LOS CAMPOS NUMERICOS:
        binding.ETdistanceRace.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.ETdesnivRace.setRawInputType(InputType.TYPE_CLASS_NUMBER)

        binding.ETdateRace.setOnClickListener{
            showDatePickerDialog()
        }

        binding.BTraceAdd.setOnClickListener{
            addRace()
        }

        binding.BTaddImageRace.setOnClickListener {
            subirImagenRace()
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

    private fun showDatePickerDialog(){
        val datePicker = DatePickerRace { day, month, year -> onDateSelected(day, month, year)}
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day:Int, month:Int, year:Int) {
        binding.ETdateRace.setText("$day/${month+1}/$year")
    }



        //FUNCION ABRIR GALERIA
    fun abreGaleria(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestGalleryPermission()
        }
    }

    fun subirImagenRace(){
        checkGalleryPermission()
    }



        private fun checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            //permiso no aceptado
            requestCameraPermission()
        }else{
            //abrir camara
            openCamera()
        }
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
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY){
              savinImg.startSavingImg()

            val imgFoto = data?.data


            val folder : StorageReference = FirebaseStorage.getInstance().getReference().child("user")
           val filename : StorageReference = folder.child("file" + imgFoto!!.lastPathSegment)
           // val filename : StorageReference = folder.child("file" + uri.lastPathSegment)
            filename.putFile(imgFoto).addOnSuccessListener {
                //ruta = it.storage.downloadUrl.toString()
                ruta = it.storage.name
                //ruta = it.storage.path
                //filename.downloadUrl
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

    private fun requestCameraPermission() {
       if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
           //el usuario ya ha rechazado los permisos
           Toast.makeText(this, "Permisos rechazados", Toast.LENGTH_SHORT).show()
       }else{
           //pedir permisos
           ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 777)
       }
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

    fun abrirMisCarreras(){
        val intent = Intent(this, carreras::class.java)
        startActivity(intent)
    }

    fun addRace(){
        if(binding.ETtitleRace.text.isNullOrBlank() || binding.ETdistanceRace.text.isNullOrBlank() || binding.ETdesnivRace.text.isNullOrBlank() ||
                binding.ETdateRace.text.isNullOrBlank() || binding.ETlocalizrace.text.isNullOrBlank()){
            showAlert("Atención" , "No pueden haber campos vacios")
        }else{
            val datosRace = hashMapOf(
                    "uid_user" to uid_user,
                    "name_race" to binding.ETtitleRace.text.trim().toString(),
                    "distance_race" to binding.ETdistanceRace.text.trim().toString().toInt(),
                    "desnivel_race" to binding.ETdesnivRace.text.trim().toString().toInt(),
                    "date_race" to binding.ETdateRace.text.trim().toString(),
                    "localizacion" to binding.ETlocalizrace.text.trim().toString(),
                    "imagen" to ruta
            )
            db.collection("carreras")
                    .add(datosRace)
                    .addOnSuccessListener {
                       // showAlert("Datos guardados", "Carrera guardada con éxito")
                        Toast.makeText(this, "Carrera añadida con éxito", Toast.LENGTH_SHORT).show()
                        abrirMisCarreras()
                    }
                    .addOnFailureListener{
                       // showAlert("Error", "No se pudieron guardar los datos")
                        Toast.makeText(this, "Error, no se pudo guardar la carrera", Toast.LENGTH_SHORT).show()
                    }

        }
    }
}