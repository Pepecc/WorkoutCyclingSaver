package com.example.proyectodam

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_custom_view_datosts.view.*

class DatosTSadapter (val ts: ArrayList<DatosRVtimeSplit>, val clickListener: (DatosRVtimeSplit) -> Unit):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DataHolder(layoutInflater.inflate(R.layout.item_custom_view_datosts, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position:Int){
            holder as DataHolder
            holder.render(ts[position], clickListener)
    }

    override fun getItemCount(): Int {
        return ts.size
    }


    class DataHolder(val view: View):RecyclerView.ViewHolder(view){
        fun render(ts: DatosRVtimeSplit, clickListener: (DatosRVtimeSplit) -> Unit){
            view.TVcardLapTS.text = "Distancia  "+ts.dist+" km"
            view.TVcardTimeTotalTS.text = "${ts.minutos} min : ${ts.segundos} sec"
            view.TVcardTramoTS.text = "Tramo:  "+ts.tramo
            view.TVcardNotasTS.text = "Notas:  "+ts.notas
        }
    }



}