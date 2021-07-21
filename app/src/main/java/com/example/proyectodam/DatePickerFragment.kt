package com.example.proyectodam

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val listener: (day:Int, month:Int, year:Int) -> Unit): DialogFragment(),
        DatePickerDialog.OnDateSetListener{
    //LA CLASE DIALOG FRAGMENT PERMITE MOSTRAR LOS DIALOGOS CON LOS QUE VAMOS A TRABAJAR

    override fun onDateSet(view:DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c : Calendar = Calendar.getInstance()
        val day : Int = c.get(Calendar.DAY_OF_MONTH)
        val month : Int = c.get(Calendar.MONTH)
        val year : Int = c.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity as Context, R.style.datePickerTheme, this, year, month, day)
       c.add(Calendar.MONTH,0)
       // picker.datePicker.minDate = c.timeInMillis
        //picker.datePicker.minDate = c.timeInMillis
        //c.add(Calendar.YEAR, 0)
       // picker.datePicker.
        picker.datePicker.maxDate = c.timeInMillis
        return picker
    }


}