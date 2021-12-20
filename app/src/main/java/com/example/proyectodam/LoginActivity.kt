package com.example.proyectodam

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.databinding.ActivityLoginBinding
import com.example.proyectodam.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var db = Firebase.firestore
    private val loading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.BTcrearUser.setOnClickListener {
            crearUsuario()
        }

        binding.TVaceptarTerms.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    private fun crearUsuario() { //ABRE LA ACTIVITY MENU SI SE REGISTRA BIEN:
         if (binding.ETnewUser.text!!.isEmpty() && binding.ETnewPass.text!!.isEmpty() && binding.ETconfirmPass.text!!.isEmpty()) {
            showAlert("Error", "No pueden haber campos vacios")
         }else if(!validarEmail(binding.ETnewUser.text.toString())){
            showAlert("Error", "Email incorrecto")
         }else if (binding.ETnewPass.text.toString() != binding.ETconfirmPass.text.toString()){
             showAlert("Error", "Las contraseñas no coinciden")
         }else if (binding.ETnewPass.text!!.length < 6 && binding.ETconfirmPass.length() < 6){
             showAlert("Error", "La contraseña ha de tener 6 caracteres")
         }else if(!CBterms.isChecked){
             showAlert("Atención", "Debes aceptar los términos y condiciones")
         }
         else{
             val email = binding.ETnewUser.text.toString()
             val pass = binding.ETnewPass.text.toString()
             loading.startLoading()
             FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                 if (it.isSuccessful) {
                     val user = FirebaseAuth.getInstance().currentUser.uid
                     //showAlert("Login", "Usuario creado con éxito")

                     db.collection("user_col").document().set(
                             //AQUI PONER UN LOADING DIALOG
                         hashMapOf(
                             "correo" to email,
                                "uid" to user
                         )
                     )
                        showMenu(it.result?.user?.email?:"", ProviderType.BASIC)
                        loading.isDimiss()
                        Toast.makeText(applicationContext, "Usuario creado con éxito" , Toast.LENGTH_SHORT).show()
                 } else {
                     Toast.makeText(applicationContext, "Error, no se ha podido crear el usuario" , Toast.LENGTH_SHORT).show()
                 }
             }
     }
    }
}
