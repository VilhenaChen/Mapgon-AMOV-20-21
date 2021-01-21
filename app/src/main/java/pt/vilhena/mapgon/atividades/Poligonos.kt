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
import kotlinx.android.synthetic.main.activity_poligonos.*
import kotlinx.android.synthetic.main.entrada_jogador.view.*
import kotlinx.android.synthetic.main.entrada_poligono.view.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados
import pt.vilhena.mapgon.logica.Jogador

class Poligonos : AppCompatActivity()  {
    lateinit var dados : Dados
    var adapterPoligonosTop : PoligonosTopAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poligonos)

        dados = intent.getSerializableExtra("Dados")!! as Dados

        adapterPoligonosTop = PoligonosTopAdapter(this, dados.getPoligonos())
        grelhaPoligonos.adapter = adapterPoligonosTop

        grelhaPoligonos.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, TopJogadores::class.java)
            intent.putExtra("dados", dados)
            intent.putExtra("posicaoPoli", position)
            startActivity(intent)
            finish()
        }

    }

    class PoligonosTopAdapter : BaseAdapter {
        var arrayPolis = ArrayList<String>()
        var context : Context? = null

        constructor(context: Context, arrayPolis : ArrayList<String>) : super(){
            this.context = context
            this.arrayPolis = arrayPolis
        }

        override fun getCount(): Int {
            return arrayPolis.size
        }

        override fun getItem(position: Int): Any {
            return arrayPolis[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val poli = this.arrayPolis[position]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var jogadorView= inflator.inflate(R.layout.entrada_poligono, null)
            jogadorView.entradaPoligono.text = poli

            return jogadorView
        }
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