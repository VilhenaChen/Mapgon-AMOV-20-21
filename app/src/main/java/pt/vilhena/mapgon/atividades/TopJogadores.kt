package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_define_equipa.*
import kotlinx.android.synthetic.main.activity_poligonos.*
import kotlinx.android.synthetic.main.activity_top_jogadores.*
import kotlinx.android.synthetic.main.entrada_jogador.view.*
import kotlinx.coroutines.delay
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados
import pt.vilhena.mapgon.logica.Jogador
import kotlin.properties.Delegates

class TopJogadores : AppCompatActivity() {
    lateinit var dados : Dados
    var posicao = 0
    var adapterTopScore : TopScoreAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_jogadores)

        dados = intent.getSerializableExtra("dados")!! as Dados
        posicao = intent.getIntExtra("posicaoPoli",0)!!

        PoligonoTOP.text = dados.getPoligonos()[posicao]

        adapterTopScore = TopScoreAdapter(this,dados)

        grelhaTopEquipas.adapter = adapterTopScore

        grelhaTopEquipas.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, TopJogdoresSelecionado::class.java)
            intent.putExtra("dados", dados)
            intent.putExtra("Poligono", dados.getPoligonos()[posicao])
            val db = Firebase.firestore
            var team = ""
            var pos = position
            val c = db.collection("Poligonos").document("Decagono")
            db.runTransaction { transation ->
                val doc = transation.get(c)
                pos += 1
                team = doc.getString(pos.toString())!!
                null
            }
            while(team.isEmpty()){}
            intent.putExtra("Equipa", team)
            startActivity(intent)
            finish()
        }
    }

    class TopScoreAdapter : BaseAdapter {
        var context : Context? = null
        var dados : Dados
        constructor(context: Context, dados : Dados) : super(){
            this.context = context
            this.dados = dados
        }

        override fun getCount(): Int {
            return 5
        }

        override fun getItem(position: Int): Any {
            return ""
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val db = Firebase.firestore
            var team = ""
            var pos = position
            val c = db.collection("Poligonos").document("Decagono")
            db.runTransaction { transation ->

                val doc = transation.get(c)
                pos += 1
                team = doc.getString(pos.toString())!!

                null
            }
            while(team.isEmpty()){Log.d("LOOP","")}
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var jogadorView= inflator.inflate(R.layout.entrada_jogador, null)
            jogadorView.entradaNomeJogador.text = team

            return jogadorView
        }
    }
    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Poligonos::class.java)
        intent.putExtra("Dados", dados);
        startActivity(intent)
        finish()
    }
}