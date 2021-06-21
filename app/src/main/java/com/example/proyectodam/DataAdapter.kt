package com.example.proyectodam
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodam.databinding.ActivityVisorTrainIndoorBinding
import kotlinx.android.synthetic.main.item_custom_view.view.*
import kotlinx.android.synthetic.main.item_custom_view_outdoor.view.*
import android.content.Intent
import android.widget.ExpandableListView

private const val tipo_indoor: Int = 0
private const val tipo_outdoor: Int = 1

private const val TAG = "VALOR DEL INT: "

class DataAdapter(val datos:ArrayList<DatosRVoutdoor>, val clickListener: (DatosRVoutdoor) -> Unit):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == tipo_indoor){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_custom_view, parent, false)
            DataHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_custom_view_outdoor, parent, false)
            DataHolderOutdoor(view)
        }
                                            //,val clickListener : (DatosRVbicis) -> Unit
    }

    override fun getItemViewType(position: Int): Int {
        return if((datos[position].tipo) == "indoor"){
           tipo_indoor
        } else{
            return tipo_outdoor
       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       // println("valor ON BIND VIEW HOLDER: "+position.toString())
        if(getItemViewType(position) == tipo_indoor){
            (holder as DataHolder).render(datos[position], clickListener)
        }else{
            (holder as DataHolderOutdoor).render(datos[position], clickListener)
        }
    }
    

    override fun getItemCount(): Int {
       return datos.size
    }

    //PARA LOS ENTRENAMIENTOS INDOOR:
    class DataHolder(val view: View):RecyclerView.ViewHolder(view){
        fun render(datos: DatosRVoutdoor, clickListener: (DatosRVoutdoor) -> Unit){

            view.TVcardFecha.text = "Fecha: " +datos.fecha
            view.TVcardTitle.text =  "Entrenamiento indoor"
            view.TVcardTotalTime.text = "Duración: "+datos.tiempo.toString()+"minutos"
            view.TVcalorInd.text = "Calorías quemadas: " +datos.calorias.toString()

            view.cardTrain.setOnClickListener {
                clickListener(datos)
            }
        }//FUN RENDER
    }//DATAHOLDER 1


    //VIEWHOLDER PARA LOS ENTRENAMIENTOS OUTDOOR:
    class DataHolderOutdoor(val view: View):RecyclerView.ViewHolder(view){
        fun render(datos:DatosRVoutdoor, clickListener: (DatosRVoutdoor) -> Unit){
               view.TVcardFechaOutdoor.text = "Fecha "+datos.fecha
               view.TVcardTotalTimeOutdoor.text = "Duración: "+datos.tiempo.toString()+"minutos"
               view.TVcalorOutdoor.text = "Calorías quemadas: "+datos.calorias.toString()
               view.TVcardTitleOutdoor.text = "Entrenamiento outdoor"
               view.TVkmOutdoor.text = datos.km.toString()+" kilómetros"

            view.cardTrainOutdoor.setOnClickListener {
                clickListener(datos)
            }
        }
    }//DATAHOLDER


}


