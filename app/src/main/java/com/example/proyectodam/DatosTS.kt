package com.example.proyectodam

data class DatosTS (
        val idUser : String,
        val idSesion: String,
        val tramo : String,
        val minutos : Int,
        val segundos : Int,
        val dist : Float,
        val notas : String
        )