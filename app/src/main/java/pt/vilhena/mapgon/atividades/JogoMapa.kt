package pt.vilhena.mapgon.atividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_jogo.*

import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados

class JogoMapa : AppCompatActivity() {
    lateinit var dados : Dados
    lateinit var countdown_timer: CountDownTimer
    var time_in_milli_seconds : Long = 0
    var inicio_Coutdown : Long = 3600000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jogo_mapa)

        dados = intent.getSerializableExtra("Dados") as Dados
        if(intent.hasExtra("TempoRestante")){
            inicio_Coutdown = intent.getLongExtra("TempoRestante", 3600000)
        }
        Countdown()

    }

    //Thread responsavel pelo countdown da aplicacao 1 hora
    fun Countdown() {
        countdown_timer = object : CountDownTimer(inicio_Coutdown.toLong(),1000) {
            override fun onTick(millisUntilFinished: Long) {
                time_in_milli_seconds = millisUntilFinished
                //updateText()
            }

            override fun onFinish() {
                runOutOfTime()
            }
        }
        countdown_timer.start()
    }

    //Funcao que atualiza o texto do Countdown
    /*fun updateText() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60
        timer.text = "$minute:$seconds"
    }*/

    //Caso o countdown chegue ao fim esta funcao redireciona os jogadores para o Fim de Jogo
    fun runOutOfTime() {
        val intent = Intent(this, FimJogo::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }



    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Jogo::class.java)
        intent.putExtra("Dados", dados)
        intent.putExtra("TempoRestante", time_in_milli_seconds)
        startActivity(intent)
        finish()
    }

}