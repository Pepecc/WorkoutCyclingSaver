package com.example.proyectodam

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.databinding.ActivityEstadisticasBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Estadisticas : AppCompatActivity() {

    //INSTANCIA DE LA CONEXION:
    private var db = Firebase.firestore

    //ID USUARIO PREFS
    private var uid_user = UserApp.prefs.getUserUid()

    //BINDING:
    private lateinit var binding : ActivityEstadisticasBinding

    //VARIABLES GLOBALES PARA GENERAR LAS ESTADÍSTICAS
     var totalTimeIndoor : Int = 0
     var totalTimeOutdoor : Int = 0
     var totalTrain: Int = 10
     var contIndoor : Int = 0
     var contOutdoor : Int = 0
     var totalCalsIndoor : Int = 0
     var totalCalsOutdoor : Int = 0
     var pulsoMedIndoor : Int = 0
     var pulsoMaxIndor : Int = 0
     var pulsoMedOutdoor : Int = 0
     var pulsoMaxOutdoor : Int = 0
     var vMed : Int = 0
     var vMax : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEstadisticasBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupTarta()

        validarBD()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MenuInicial::class.java))
    }

    //FUNCIÓN PARA EL DIÁLOGO DE ALERTA:
    fun showAlert(titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    //VALIDAR QUE EL USUARIO TIENE DATOS EN FIREBASE:
    private fun validarBD(){
        db.collection("entrenamientos")
                .whereEqualTo("uid_user", uid_user).get().addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        showAlert("Atención", "No hay estadísticas porque aún no has guardado ningún entrenamiento.")
                    }
                    else{
                        calcularIndoor()
                        calcularOutdoor()
                    }
                }
    }
    //CONTAR LOS ENTRENAMIENTOS INDOOR Y EL TIEMPO ENTRENADO:
    private fun calcularIndoor(){
        db.collection("entrenamientos")
                .whereEqualTo("uid_user", uid_user)
                .whereEqualTo("tipo_train", "indoor")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents){
                        totalTimeIndoor = document.data.get("total_time").toString().toInt() + totalTimeIndoor
                        totalCalsIndoor = document.data.get("calorias").toString().toInt() + totalCalsIndoor
                        pulsoMedIndoor = document.data.get("pulso_med").toString().toInt() +  pulsoMedIndoor
                        pulsoMaxIndor = document.data.get("pulso_med").toString().toInt() + pulsoMaxIndor
                        contIndoor = documents.size()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }.addOnCompleteListener{
            
                }
    }
    //CONTAR LOS ENTRENAMIENTOS OUTDOOR Y EL TIEMPO ENTRENADO:
    private fun calcularOutdoor(){
        db.collection("entrenamientos")
                .whereEqualTo("uid_user", uid_user)
                .whereEqualTo("tipo_train", "outdoor")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents){
                        totalTimeOutdoor = document.data.get("total_time").toString().toInt() + totalTimeOutdoor
                        totalCalsOutdoor = document.data.get("calorias").toString().toInt() + totalCalsOutdoor
                        pulsoMedOutdoor = document.data.get("pulso_med").toString().toInt() + pulsoMedOutdoor
                        pulsoMaxOutdoor = document.data.get("pulso_max").toString().toInt() + pulsoMaxOutdoor
                        vMed = document.data.get("velo_med").toString().toInt() + vMed
                        vMax = document.data.get("velo_max").toString().toInt() + vMax
                        contOutdoor = documents.size()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }.addOnCompleteListener{
                    calcularPrimerChart()
                    calcularSegundoChart()
                    calcularTercerChart()
                    calcularCuartoChart()
                    calcularQuintoChart()
                }
    }

    //CALCULAR EL PORCENTAJE DE CADA ENTRENAMIENTO Y PASARSELO A LA FUNCIÓN QUE LO PINTA
    private fun calcularPrimerChart(){
        totalTrain = contIndoor+contOutdoor
        val contIndFloat : Float = contIndoor.toFloat()
        val contOutFloat : Float = contOutdoor.toFloat()
        val calculoID : Float = (contIndFloat/totalTrain)*100
        val calculoOD : Float = (contOutFloat/totalTrain)*100

        //PINTA LOS DATOS DE LA TARTA
        rellenarDatosChart(calculoID, calculoOD)
    }

    private fun calcularSegundoChart(){
        val totalIND : Float = totalTimeIndoor/60.toFloat()
        val totalOUT : Float = totalTimeOutdoor/60.toFloat()
        rellenarDatosBarra(totalIND, totalOUT)
    }

    private fun calcularTercerChart(){
        val totalCalsID : Float = totalCalsIndoor.toFloat()
        val totalCalsOD : Float = totalCalsOutdoor.toFloat()
        barraVertical(totalCalsID, totalCalsOD)
    }

    private fun calcularCuartoChart(){
        val mediaPuls : Float = (pulsoMedIndoor+pulsoMedOutdoor)/totalTrain.toFloat()
        val maxPuls : Float = (pulsoMaxIndor+pulsoMaxOutdoor)/totalTrain.toFloat()
        println("MEDIA PULSO TOTAL" + mediaPuls)
        println("MAX PULSO TOTAL" + maxPuls)
        lineChart(mediaPuls, maxPuls)
    }

    private fun calcularQuintoChart(){
        val vMedF : Float = (vMed/contOutdoor).toFloat()
        val vMaxF : Float = (vMax/contOutdoor).toFloat()
        rellenarBarraVelocidad(vMedF, vMaxF)
    }
    //PULSO:
    private fun lineChart(med: Float, max: Float){
      val datosLinea = ArrayList<Entry>()
        datosLinea.add(Entry(1f, 100f))
        datosLinea.add(Entry(2f, med))
        datosLinea.add(Entry(3f, max))
        datosLinea.add(Entry(4f, 220f))

        val v1 = LineDataSet(datosLinea, "Valores medios y máximos de pulso")
        v1.setDrawValues(true)
        v1.setDrawFilled(true)
        v1.lineWidth = 3f

        v1.fillColor = R.color.yellow
        v1.fillAlpha = R.color.verderolo

        binding.CVstatsFour.xAxis.labelRotationAngle = 0f
        binding.CVstatsFour.data = LineData(v1)

        binding.CVstatsFour.axisRight.isEnabled = false
        binding.CVstatsFour.xAxis.axisMaximum = 4f

        binding.CVstatsFour.xAxis.axisMinimum = 1f

        binding.CVstatsFour.setTouchEnabled(true)
        binding.CVstatsFour.setPinchZoom(true)

        binding.CVstatsFour.description.text = ""
        binding.CVstatsFour.setNoDataText("No forex yet")

        binding.CVstatsFour.animateX(1800, Easing.EaseInExpo)
    }
    //CALORÍAS:
    private fun barraVertical(calsID: Float, calsOD: Float){
        val datosBarra = ArrayList<BarEntry>()
        datosBarra.add(BarEntry(0f, calsOD))
        datosBarra.add(BarEntry(1f, calsID))
        val dataSet = BarDataSet(datosBarra, "Calorías quemadas durante entrenamiento outdoor e indoor")
        dataSet.setDrawIcons(true)
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        val data = BarData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(20f)
        data.setValueTextColor(Color.BLACK)
        binding.CVstatsThird.data = data
        binding.CVstatsThird.highlightValue(null)
        binding.CVstatsThird.invalidate()
        binding.CVstatsThird.animateXY(2000, 2000)
    }

    //VELOCIDAD MEDIA Y MAXIMA
    private fun rellenarBarraVelocidad(med: Float, max: Float){
        val datosVelo = ArrayList<BarEntry>()
        datosVelo.add(BarEntry(2f, med, "Velocidad media"))
        datosVelo.add(BarEntry(1f, max, "Velocidad máxima"))
        val dataSet = BarDataSet(datosVelo, "Velocidades media y máxima de los entrenamientos")

        dataSet.setDrawIcons(true)
        dataSet.iconsOffset = MPPointF(0f,0f)
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

        val data = BarData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(20f)
        data.setValueTextColor(Color.BLACK)

        binding.CVstatsFifth.data = data
        binding.CVstatsFifth.highlightValue(null)
        binding.CVstatsFifth.invalidate()
        binding.CVstatsFifth.animateXY(2000,2000)
        binding.CVstatsFifth.description.text=""
    }
    //HORAS ENTRENAMIENTO:
    private fun rellenarDatosBarra(ind: Float, out: Float){
        val datosBarra = ArrayList<BarEntry>()
            datosBarra.add(BarEntry(2f, ind, "Entrenamiento indoor"))
            datosBarra.add(BarEntry(1f, out, "Entrenamiento outdoor"))
        val dataSet = BarDataSet(datosBarra, "Tiempo en horas de los entrenamiento indoor y outdoor")

        dataSet.setDrawIcons(true)
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.setColors(*ColorTemplate.JOYFUL_COLORS)

        val data = BarData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(20f)
        data.setValueTextColor(Color.BLACK)
        binding.CVstatsSecond.data = data
        binding.CVstatsSecond.highlightValues(null)
        binding.CVstatsSecond.invalidate()
        binding.CVstatsSecond.animateXY(2000, 2000)
        binding.CVstatsSecond.description.text=""
    }
    //FUNCIÓN QUE CONFIGURA LA TARTA:
    private fun setupTarta(){
        binding.CVstatsFirst.isDrawHoleEnabled
        binding.CVstatsFirst.setUsePercentValues(true)
        binding.CVstatsFirst.setEntryLabelTextSize(18f)
        binding.CVstatsFirst.setEntryLabelColor(Color.BLACK)
        binding.CVstatsFirst.setCenterTextColor(Color.BLACK)
        binding.CVstatsFirst.setCenterTextSize(24f)
        binding.CVstatsThird.description.text=""
    }
    //FUNCIÓN QUE RELLENA LOS DATOS DE LA TARTA:
    private fun rellenarDatosChart(percentIndor: Float, percentOutdoor: Float){
        val colores = ArrayList<Int>()
        colores.add(R.color.verderolo)
        colores.add(R.color.teal_200)

        val datosTarta = ArrayList<PieEntry>()
            datosTarta.add(PieEntry(percentIndor, "Entrenamiento indoor"))
            datosTarta.add(PieEntry(percentOutdoor, "Entrenamiento outdoor"))
        val dataSet = PieDataSet(datosTarta, "Tipo de entrenamientos")

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 2f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueTextSize(20f)

        data.setValueTextColor(Color.BLACK)

        binding.CVstatsFirst.data = data
        binding.CVstatsFirst.highlightValues(null)
        binding.CVstatsFirst.invalidate()
        binding.CVstatsFirst.animateXY(2000, 2000)
    }
}