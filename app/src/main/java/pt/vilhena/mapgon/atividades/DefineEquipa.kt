package pt.vilhena.mapgon.atividades

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_define_equipa.*
import kotlinx.android.synthetic.main.entrada_jogador.view.*
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados
import pt.vilhena.mapgon.logica.Jogador

class DefineEquipa : AppCompatActivity()  {
    lateinit var dados : Dados
    var adapter : JogadoresDefineEquipaAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_define_equipa)

        dados = intent.getSerializableExtra("Dados") as Dados

        dados.getInfoEquipa()

        adapter = JogadoresDefineEquipaAdapter(this,dados.getArrayJogadores())

        grelha_JogadoresDefineEquipa.adapter = adapter

        TeamIDText.text = dados.nomeEquipa
        //Log.d("A PRIMA DO DAVID DE 4",dados.getArrayJogadores()[0].latitude)

    }

    class JogadoresDefineEquipaAdapter : BaseAdapter{
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

    //Ir para o Jogo
    fun onbtnStart(view: View) {
        dados.mudaNomeEquipa(nomeEquipa.text.toString())
        val intent = Intent(this, Jogo::class.java)
        intent.putExtra("Dados", dados);
        startActivity(intent)
        finish()
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Servidor::class.java)
        intent.putExtra("Dados", dados);
        startActivity(intent)
        finish()
    }


}