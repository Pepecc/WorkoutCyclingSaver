package com.example.proyectodam
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_custom_view_carreras.view.*
import java.io.File

class RaceAdapter(val carreras:ArrayList<DatosRVraces>, val clickListener: (DatosRVraces) -> Unit):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int): DataHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DataHolder(layoutInflater.inflate(R.layout.item_custom_view_carreras, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position:Int){
        //holder.render(carreras[position])
        holder as DataHolder
        holder.render(carreras[position], clickListener)
    }

    override fun getItemCount(): Int {
        return carreras.size
    }

    class DataHolder(val view: View):RecyclerView.ViewHolder(view){
        fun render(carreras: DatosRVraces, clickListener: (DatosRVraces) -> Unit){
            view.CVrace_name.text = carreras.name
            view.CVrace_dist.text = "Distancia: ${carreras.dist} km"
            view.CVdesnivel.text = "Desnivel acumulado: ${carreras.desniv} +M"
            view.CVdate_race.text = "Fecha: ${carreras.date}"
            val imagen = carreras.rutaImg

                //MOSTRAR LA IMAGEN:
                val storageRef = FirebaseStorage.getInstance().reference.child("user/$imagen")
                val localfile = File.createTempFile("tempImage", "jpg")
                storageRef.getFile(localfile).addOnSuccessListener {

                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    view.imageView5.setImageBitmap(bitmap)


                }.addOnFailureListener{
                }
            view.cardRaces.setOnClickListener {
                clickListener(carreras)
            }
        }
    }
}