package com.example.proyectodam
import android.app.Application

class UserApp: Application() {
    companion object {
        //ESTA INSTANCIA DE PREFS SE VA A INICIAR MAS TARDE
        lateinit var prefs: Prefs
    }

    override fun onCreate(){
        super.onCreate()
        prefs = Prefs(applicationContext)
    }//
}