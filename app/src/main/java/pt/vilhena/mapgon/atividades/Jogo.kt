package pt.vilhena.mapgon.atividades

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cliente.*
import kotlinx.android.synthetic.main.activity_define_equipa.*
import kotlinx.android.synthetic.main.activity_jogo.*
import kotlinx.android.synthetic.main.entrada_angulo.view.*
import kotlinx.android.synthetic.main.entrada_coordenada.view.*
import kotlinx.android.synthetic.main.entrada_distancia.view.*
import kotlinx.android.synthetic.main.entrada_jogador.view.*
import kotlinx.coroutines.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados
import pt.vilhena.mapgon.logica.Jogador
import java.math.RoundingMode
import javax.security.auth.callback.Callback
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class Jogo : AppCompatActivity()  {

    lateinit var countdown_timer: CountDownTimer
    var time_in_milli_seconds : Long = 0
    var inicio_Coutdown : Long = 3600000
    lateinit var dados : Dados
    var adapterCoordenadas : CoordenadasJogoAdapter? = null
    var adapterDistancias : DistanciasJogoAdapter? = null
    var adapterAngulos : AngulosJogoAdapter? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude : String = ""
    var longitude : String = ""
    val request = LocationRequest()
    var callback = LocationCallback()
    var mainscope = MainScope()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jogo)
        dados = intent.getSerializableExtra("Dados") as Dados
        if(intent.hasExtra("TempoRestante")){
            inicio_Coutdown = intent.getLongExtra("TempoRestante", 3600000)
        }
        dados.getInfoEquipa()
        adapterCoordenadas = CoordenadasJogoAdapter(this, dados.getArrayJogadores())
        adapterDistancias = DistanciasJogoAdapter(this, dados.getArrayJogadores(), dados)
        adapterAngulos = AngulosJogoAdapter(this, dados.getArrayJogadores(), dados)


        Countdown()
        request.interval=30000
        request.fastestInterval = 15000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        jogPolig.text = "J${dados.idProprio} - ${dados.poli}"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {

                    callback = object : LocationCallback(){
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location : Location? = locationResult.lastLocation
                        if(location!=null)
                        {
                            latitude = location!!.latitude.toString()
                            longitude = location!!.longitude.toString()
                            dados.getInfoEquipa()
                            dados.atualizaDB(
                                dados.getArrayJogadores()[dados.idProprio-1].nome,
                                latitude,
                                longitude
                            )
                            dados.getInfoEquipa()
                            grelhaCoordenadasJogo.adapter = adapterCoordenadas
                            grelhaDistanciaJogo.adapter = adapterDistancias
                            grelhaAnguloJogo.adapter = adapterAngulos
                            poligonoFormado(dados.verificaPoligono())

                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(request,
                    callback as LocationCallback,null)
        }
    }

    class CoordenadasJogoAdapter : BaseAdapter {
        var arrayJogadores = ArrayList<Jogador>()
        var context : Context? = null

        constructor(context: Context, arrayJogadores : ArrayList<Jogador>) : super(){
            this.context = context
            this.arrayJogadores = arrayJogadores
        }

        override fun getCount(): Int {
            return arrayJogadores.size
        }

        override fun getItem(position: Int): Any {
            return arrayJogadores[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val jogador = this.arrayJogadores[position]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var jogadorView= inflator.inflate(R.layout.entrada_coordenada, null)
            jogadorView.entradaCoordenadasJogador.text = "J${jogador.id} - ${jogador.latitude} ; ${jogador.longitude}"

            return jogadorView
        }
    }

    class DistanciasJogoAdapter : BaseAdapter {
        var arrayJogadores = ArrayList<Jogador>()
        var context : Context? = null
        var dados : Dados

        constructor(context: Context, arrayJogadores : ArrayList<Jogador>, dados : Dados) : super(){
            this.context = context
            this.arrayJogadores = arrayJogadores
            this.dados = dados
        }

        override fun getCount(): Int {
            return arrayJogadores.size
        }

        override fun getItem(position: Int): Any {
            return arrayJogadores[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var jogador1 = this.arrayJogadores[position]
            var pos: Int = position
            if(position!=(arrayJogadores.size-1)){
                pos = pos + 1
            }
            else {
                pos = 0
            }
            var jogador2 = this.arrayJogadores[pos]
            var dist = dados.getFuncoesCoordenadas().haversine(jogador1.latitude.toDouble(),jogador1.longitude.toDouble(),jogador2.latitude.toDouble(),jogador2.longitude.toDouble())

            dist *= 1000

            var distFloat= dist.toFloat()

            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var jogadorView= inflator.inflate(R.layout.entrada_distancia, null)
            jogadorView.entradaDistanciasJogador.text = "J${jogador1.id} - J${jogador2.id} - ${distFloat}m"


            return jogadorView
        }
    }

    class AngulosJogoAdapter : BaseAdapter {
        var arrayJogadores = ArrayList<Jogador>()
        var context : Context? = null
        var dados : Dados

        constructor(context: Context, arrayJogadores : ArrayList<Jogador>, dados : Dados) : super(){
            this.context = context
            this.arrayJogadores = arrayJogadores
            this.dados = dados
        }

        override fun getCount(): Int {
            return arrayJogadores.size
        }

        override fun getItem(position: Int): Any {
            return arrayJogadores[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val jogadorcentro = this.arrayJogadores[position]
            var pos: Int = position
            if(position != 0){
                pos = position - 1
            }
            else{
                pos = (arrayJogadores.size-1)
            }
            val jogadorantes = this.arrayJogadores[pos]
            if(position != (arrayJogadores.size-1)){
                pos = position + 1
            }
            else{
                pos = 0
            }
            val jogadordepois = this.arrayJogadores[pos]

            var angulo = dados.getFuncoesCoordenadas().CalculaAngulo(jogadorantes.latitude.toDouble(), jogadorantes.longitude.toDouble(), jogadorcentro.latitude.toDouble(), jogadorcentro.longitude.toDouble(), jogadordepois.latitude.toDouble(), jogadordepois.longitude.toDouble())
            var anguloFloat= angulo.toFloat()
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var jogadorView= inflator.inflate(R.layout.entrada_angulo, null)
            jogadorView.entradaAngulosJogador.text = "J${jogadorcentro.id} - ${anguloFloat}º"

            return jogadorView
        }
    }

    //Thread responsavel pelo countdown da aplicacao 1 hora
    fun Countdown() {
        countdown_timer = object : CountDownTimer(inicio_Coutdown.toLong(),1000) {
            override fun onTick(millisUntilFinished: Long) {
                time_in_milli_seconds = millisUntilFinished
                updateText()
            }

            override fun onFinish() {
                runOutOfTime()
            }
        }
        countdown_timer.start()
    }

    //Funcao que atualiza o texto do Countdown
    fun updateText() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60
        timer.text = "$minute:$seconds"
    }


    // Ir para o Mapa
    fun onMap(view: View) {
        val intent = Intent(this, JogoMapa::class.java)
        intent.putExtra("Dados", dados)
        intent.putExtra("TempoRestante", time_in_milli_seconds)
        startActivity(intent)
        finish()
    }

    //Caso o countdown chegue ao fim esta funcao redireciona os jogadores para o Fim de Jogo
    fun runOutOfTime() {
        val intent = Intent(this, FimJogo::class.java)
        intent.putExtra("Dados", dados)
        fusedLocationClient.removeLocationUpdates(callback as LocationCallback)
        stopCoroutine()
        startActivity(intent)
        finish()
    }

    //Muda a visibilidade do Botao, caso o poligono ja esteja formado
    private fun poligonoFormado(visivel : Boolean) {
        if(visivel) {
            btnAcabarJogo.visibility = View.VISIBLE
            poliIcon.setImageResource(R.drawable.ic_action_name)

        } else {
            btnAcabarJogo.visibility = View.INVISIBLE
            poliIcon.setImageResource(R.drawable.ic_cross)
            stopCoroutine()
        }

    }

    fun startCoroutineVerifica(){
        mainscope.launch(Dispatchers.Default){
            val db = Firebase.firestore
            var flag : Boolean = true
            while(true) {
                for(i in dados.getArrayJogadores().indices){
                    val c = db.collection("Equipas").document(dados.nomeEquipa).collection(dados.getArrayJogadores()[i].nome)
                        .document("coordenadas")

                    db.runTransaction { transation ->
                        val doc = transation.get(c)
                        if(!doc.getBoolean("Acabou")!!)
                        {
                            flag = false
                        }
                        null
                    }
                }
                delay(1000)
            }
            if(flag){
                runOutOfTime()
            }
        }

    }
    fun stopCoroutine()
    {
        mainscope.cancel()
    }

    fun onEnd(view: View) {
        val db = Firebase.firestore
        var v = db.collection("Equipas").document(dados.nomeEquipa).collection(dados.getArrayJogadores()[dados.idProprio].nome).document("coordenadas")
        db.runTransaction { transition ->
            //val doc = transition.get(v)
            transition.update(v,"Acabou",true)
            null
        }
        for (i in dados.getArrayJogadores().indices) {
            v = db.collection("Equipas").document(dados.nomeEquipa).collection(dados.getArrayJogadores()[i].nome)
                .document("coordenadas")
            db.runTransaction { transition ->
                val doc = transition.get(v)
                doc.getBoolean("Acabou")
                null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(callback as LocationCallback)
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Dados", dados)
        fusedLocationClient.removeLocationUpdates(callback as LocationCallback)
        startActivity(intent)
        finish()
    }


}


