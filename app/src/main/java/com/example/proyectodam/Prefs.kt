package com.example.proyectodam

import android.content.Context

class Prefs (context: Context){
    val SHARED_NAME: String = "Mydtb"
    val SHARED_USER_UID = "user_uid"
    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    //GUARDAR UID DEL USUARIO:
    fun saveUserUid (user_uid: String){
        storage.edit().putString(SHARED_USER_UID, user_uid).apply()
    }

    //RECUPERAR UID USUARIO:
    fun getUserUid(): String {
        return storage.getString(SHARED_USER_UID, "")!!
    }
}