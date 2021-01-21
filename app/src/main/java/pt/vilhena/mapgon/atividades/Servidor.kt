
package pt.vilhena.mapgon.atividades

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_servidor.*
import kotlinx.android.synthetic.main.entrada_jogador.view.*
import kotlinx.coroutines.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados
import pt.vilhena.mapgon.logica.Jogador


const val SERVER_MODE = 0
const val CLIENT_MODE = 1

class Servidor : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude : String = ""
    var longitude : String = ""
    var coordenadas : String =""
    lateinit var dados : Dados
    var mainscope = MainScope()
    //var adapterSevidor : ServidorAdapter? = null

    //private lateinit var model : ModeloVistaJogo
    //private var dlg : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servidor)
        dados = intent.getSerializableExtra("Dados") as Dados
        //model = ViewModelProvider(this).get(ModeloVistaJogo::class.java)

        //adapterSevidor = ServidorAdapter(this, dados.getArrayJogadores())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val permission =ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location!!.latitude.toString()
                    longitude = location!!.longitude.toString()
                    coordenadas = latitude + " ; " + longitude
                    coordenadasIniciais.text = coordenadas
                    getIPServidor()
                    dados.setIDEquipa(IPServer.text.toString())
                    dados.mudaNomeEquipa("")
                    dados.adicionaJogador(latitude, longitude)
                    dados.criaBD()
                    dados.idProprio = 1
                }
            }
        }
        //while(dados.idProprio!=1){Log.d("THREAD","AQUI")}
        //startCorroutine()





        /*model.connectionState.observe(this) {
            if (it != ModeloVistaJogo.ConnectionState.SETTING_PARAMETERS &&
                    it != ModeloVistaJogo.ConnectionState.SERVER_CONNECTING && dlg?.isShowing == true) {
                dlg?.dismiss()
                dlg = null
            }

            if (it == ModeloVistaJogo.ConnectionState.CONNECTION_ERROR ||
                    it == ModeloVistaJogo.ConnectionState.CONNECTION_ENDED)
                finish()
        }
        if (model.connectionState.value != ModeloVistaJogo.ConnectionState.CONNECTION_ESTABLISHED) {
                startAsServer()
        }*/
    }

    /*fun startCorroutine(){
        mainscope.launch(Dispatchers.Default){
            while(true) {
                dados.getInfoEquipa()
                grelha_JogadoresServidor.adapter = adapterSevidor
                delay(2000)
            }
        }
    }*/
    class ServidorAdapter : BaseAdapter {
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
            var jogadorView= inflator.inflate(R.layout.entrada_jogador, null)
            jogadorView.entradaNomeJogador.text = jogador.nome

            return jogadorView
        }
    }


    //Mostra o IP do Servidor
    fun getIPServidor() {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = wifiManager.connectionInfo.ipAddress
        val strIPAddress = String.format("%d.%d.%d.%d",
            ip and 0xff,
            (ip shr 8) and 0xff,
            (ip shr 16) and 0xff,
            (ip shr 24) and 0xff
        )
        IPServer.text = strIPAddress
    }

    //Escrever na Firesbase as coordenadas
    fun onCloseTeam(view: View) {
        val intent = Intent(this, DefineEquipa::class.java)
        intent.putExtra("Dados", dados);
        mainscope.cancel()
        startActivity(intent)
        finish()
    }


    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Dados", dados)
        mainscope.cancel()
        startActivity(intent)
        finish()
    }

}

