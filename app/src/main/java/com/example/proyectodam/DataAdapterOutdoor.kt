package com.example.proyectodam
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_custom_view_outdoor.view.*

class DataAdapterOutdoor(val datosOD:ArrayList<DatosRVoutdoor>):RecyclerView.Adapter<DataAdapterOutdoor.DataHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DataHolder(layoutInflater.inflate(R.layout.item_custom_view_outdoor, parent, false))
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        holder.render(datosOD[position])
      //  holder.itemView.setOnClickListener {  }
    }

    override fun getItemCount(): Int {
        return datosOD.size
    }

    class DataHolder(val view: View): RecyclerView.ViewHolder(view){

        fun render(datosOD: DatosRVoutdoor){
            if(datosOD.km == 0f){

            }
            view.TVcardFechaOutdoor.text = "Fecha "+datosOD.fecha
            view.TVcardTotalTimeOutdoor.text = "Tiempo total: "+datosOD.tiempo
            view.TVcalorOutdoor.text = "Calorías quemadas: "+datosOD.calorias
            view.TVkmOutdoor.text = "Km: "+datosOD.km
            view.TVcardTitleOutdoor.text = "Entrenamiento outdoo555r"
        }
    }



}