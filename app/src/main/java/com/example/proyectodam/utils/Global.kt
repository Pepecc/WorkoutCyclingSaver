package com.example.proyectodam.utils

import android.app.Application
import com.example.proyectodam.DatosTS

public class Global : Application() {
    companion object {
        @JvmField
        var idSesionTrain: String = ""
    }
}
