package com.example.proyectodam.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.example.proyectodam.R
import kotlinx.android.synthetic.main.saving_item.view.*

class SavingDialog (val mActivity: Activity){
    private lateinit var isdialog: AlertDialog

    private fun rotarImagen(view: View) {
        val animation = RotateAnimation(0f, 720f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 3000
        animation.repeatCount = Animation.INFINITE
        //animation.repeatMode = Animation.REVERSE
        view.startAnimation(animation)
    }

    fun startSaving(){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.saving_item, null)
        val builder = AlertDialog.Builder(mActivity)

        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()

        rotarImagen(dialogView.logorotao3)
    }

    fun isDimiss(){
        isdialog.dismiss()
    }
}