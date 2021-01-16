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
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_define_equipa.*
import kotlinx.android.synthetic.main.entrada_jogador.view.*
import kotlinx.coroutines.*
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados
import pt.vilhena.mapgon.logica.Jogador

class DefineEquipa : AppCompatActivity()  {
    lateinit var dados : Dados
    var adapter : JogadoresDefineEquipaAdapter? = null
    private val mainscope = MainScope()
    var Flag : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_define_equipa)

        dados = intent.getSerializableExtra("Dados") as Dados

        dados.getInfoEquipa()

        adapter = JogadoresDefineEquipaAdapter(this,dados.getArrayJogadores())

        grelha_JogadoresDefineEquipa.adapter = adapter

        TeamIDText.text = dados.nomeEquipa
        //Log.d("A PRIMA DO DAVID DE 4",dados.getArrayJogadores()[0].latitude)

        //Esta Coroutine serve para verificar se ja existem, ou nao 3 ou mais jogadores ligados
        mainscope.launch(Dispatchers.Default){
            val db = Firebase.firestore
            var number : Long = 0
            val c = db.collection("Equipas").document(dados.nomeEquipa)
            while(true) {
                db.runTransaction { transation ->
                    val doc = transation.get(c)
                    number = doc.getLong("nrJogadores")!!
                    Log.d("HERE",number.toString())
                    if (number >= 3) {
                        Log.d("HERE","sera")
                        Flag = true
                    }
                    Flag = false
                    null
                }
                delay(500)
            }
        }

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

    fun changeBtnVisibility() {
        mainscope.cancel()
        btnDefine.visibility= View.VISIBLE
    }

    //Ir para o Jogo
    fun onbtnStart(view: View) {
        if (Flag == true) {
            if (!nomeEquipa.text.toString().isEmpty()) {
                dados.mudaNomeEquipa(nomeEquipa.text.toString())
            }
            val db = Firebase.firestore
            val v = db.collection("Equipas").document(dados.nomeEquipa)
            db.runTransaction { transition ->
                val doc = transition.get(v)
                transition.update(v, "Comecou", true)
                null
            }
            val intent = Intent(this, Jogo::class.java)
            intent.putExtra("Dados", dados);
            startActivity(intent)
            finish()
        }
        else
        {
            Toast.makeText(this, "Precisa de ter 3 ou mais jogadores", Toast.LENGTH_SHORT).show()
            return
        }
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