package com.example.proyectodam.utils

import android.app.Application

class Global : Application() {
    companion object {
        @JvmField
        var idSesionTrain: String = ""
    }
}
