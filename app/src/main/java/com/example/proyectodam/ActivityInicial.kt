package com.example.proyectodam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectodam.databinding.ActivityInicialBinding
import com.example.proyectodam.utils.LoadingDialog
import java.util.regex.Pattern


class ActivityInicial : AppCompatActivity() {

    private lateinit var binding: ActivityInicialBinding

    private val loading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInicialBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.BTregister.setOnClickListener{
            abrirRegistro()

        }

        binding.BTlogin.setOnClickListener{
            loguearUsuario()
        }

    }

    fun showAlert(titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.finish()
    }

    private fun abrirRegistro(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun validarEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun showMenu(email: String, provider: ProviderType){
        val menuIntent = Intent(this, MenuInicial::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(menuIntent)
    }

    private fun loguearUsuario(){
        if(binding.ETuser.text.isNullOrBlank() && binding.ETpass.text.isNullOrBlank()){
            showAlert("Error", "No pueden haber campos vacios")
        }else if(!validarEmail(binding.ETuser.text.toString())){
            showAlert("Error", "Email incorrecto")
        }else if (binding.ETpass.text!!.length < 6 ){
            showAlert("Error", "La contraseña ha de tener 6 caracteres")
        }else{
            loading.startLoading()
            val email = binding.ETuser.text.toString()
            val pass = binding.ETpass.text.toString()
            //Para loguear con el usuario creado:
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    loading.isDimiss()
                    showMenu(it.result?.user?.email?:"", ProviderType.BASIC)
                    Toast.makeText(applicationContext, "Sesión iniciada $email ", Toast.LENGTH_SHORT).show()
                } else {
                    loading.isDimiss()
                    showAlert("Error", "Usuario no válido")
                }
            } //Firebase
        }
    }
}