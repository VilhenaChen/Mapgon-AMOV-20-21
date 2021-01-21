package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_define_equipa.*
import kotlinx.android.synthetic.main.activity_fim_jogo.*
import kotlinx.android.synthetic.main.entrada_coordenada.view.*
import kotlinx.android.synthetic.main.entrada_jogador.view.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados
import pt.vilhena.mapgon.logica.Jogador

class FimJogo : AppCompatActivity() {
    lateinit var dados : Dados
    var adapterFim : FimAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fim_jogo)

        dados = intent.getSerializableExtra("Dados")!! as Dados


        adapterFim = FimAdapter(this, dados.getArrayJogadores())

        dados.getInfoEquipa()

        grelhaCoordenadasFim.adapter = adapterFim

        mostraNomeEquipa()
        mostraIdEquipa()
        mostraPoligono()
        mostraDistanciaMedia()
        mostraAreaPoligono()


    }

    class FimAdapter : BaseAdapter {
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
            jogadorView.entradaCoordenadasJogador.text = "J${jogador.id} - ${jogador.latitude}; ${jogador.longitude}"

            return jogadorView
        }
    }

    fun mostraNomeEquipa(){
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(dados.nomeEquipa)
        db.runTransaction { transition ->
            val doc = transition.get(v)
            var teamName = doc.getString("NomeEquipa")!!
            nomeEquipaFim.text = teamName
            null
        }
    }

    fun mostraIdEquipa(){
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(dados.nomeEquipa)
        db.runTransaction { transition ->
            val doc = transition.get(v)
            var teamId = doc.getString("IDEquipa")!!
            idEquipaFim.text = teamId
            null
        }
    }

    fun mostraPoligono(){
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(dados.nomeEquipa)
        db.runTransaction { transition ->
            val doc = transition.get(v)
            var poligono = doc.getString("Poligono")!!
            tipoPoliFim.text = poligono
            null
        }
    }

    fun mostraDistanciaMedia(){
        distMediaFim.text = dados.calculaDistanciaMedia().toString()
    }

    fun mostraAreaPoligono(){
        areaPoliFim.text = dados.areaPoligono().toString()
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Dados", dados);
        startActivity(intent)
        finish()
    }

}