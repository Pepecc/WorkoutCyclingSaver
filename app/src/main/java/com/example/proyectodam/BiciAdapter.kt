package com.example.proyectodam
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_custom_view_bicicletas.view.*
import java.io.File


class BiciAdapter(val bicis:ArrayList<DatosRVbicis>, val clickListener: (DatosRVbicis) -> Unit):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DataHolder(layoutInflater.inflate(R.layout.item_custom_view_bicicletas, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position:Int){
            holder as DataHolder
             holder.render(bicis[position], clickListener)
    }

    override fun getItemCount(): Int {
        return bicis.size
    }

    class DataHolder(val view: View):RecyclerView.ViewHolder(view){

         fun render(bicis: DatosRVbicis, clickListener: (DatosRVbicis) -> Unit){
            view.TVmarcabici.text =  bicis.marca +" "+bicis.modelo
            view.TVtipo_bici.text = "Tipo de bici: "+bicis.tipo
            view.TVodometro.text = "Kilómetros: "+bicis.odometro.toString()
            view.TVfecha_compraCV.text = "Fecha de compra: "+bicis.fechacompra
             val imagen = bicis.rutaImg
            view.cvBicis.setOnClickListener {
                clickListener(bicis)
            }

             //MOSTRAR LA IMAGEN:
             var storageRef = FirebaseStorage.getInstance().reference.child("user/$imagen")
             var localfile = File.createTempFile("tempImage", "jpg")
             storageRef.getFile(localfile).addOnSuccessListener {

                 var bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                 view.biciImage.setImageBitmap(bitmap)

             }.addOnFailureListener{
               //  Toast.makeText(view, "Error, no se pudo descargar la imagen", Toast.LENGTH_SHORT).show()
             }

        }
        }
    }



