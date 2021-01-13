package pt.vilhena.mapgon.atividades

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_jogo.*
import kotlinx.android.synthetic.main.activity_servidor.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados

class Jogo : AppCompatActivity()  {
    lateinit var countdown_timer: CountDownTimer
    var time_in_milli_seconds = 0
    lateinit var dados : Dados
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val request = LocationRequest()
    var latitude : String = ""
    var longitude : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jogo)

        dados = intent.getSerializableExtra("Dados") as Dados

        request.interval=30000
        request.fastestInterval = 15000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationClient.requestLocationUpdates(request, object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    val location : Location? = locationResult.lastLocation
                    if(location!=null)
                    {
                        latitude = location!!.latitude.toString()
                        longitude = location!!.longitude.toString()
                    }


                }
            },null)
        }

        Countdown()
    }

    //Thread responsavel pelo countdown da aplicacao 1H
    fun Countdown() {
        countdown_timer = object : CountDownTimer(3600000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                time_in_milli_seconds = millisUntilFinished.toInt()
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
        intent.putExtra("Dados", dados)
        val intent = Intent(this, JogoMapa::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }

    //Caso o countdown chegue ao fim esta funcao redireciona os jogadores para o Fim de Jogo
    fun runOutOfTime() {
        val intent = Intent(this, FimJogo::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }

    //Muda a visibilidade do Botao, caso o poligono ja esteja formado
    private fun changeBtnVisibility() {
        btnAcabarJogo.visibility = View.VISIBLE
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }
}