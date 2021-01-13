package pt.vilhena.mapgon.atividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_define_equipa.*
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados

class DefineEquipa : AppCompatActivity()  {
    lateinit var dados : Dados
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_define_equipa)

        dados = intent.getSerializableExtra("Dados") as Dados
    }

    //Ir para o Jogo
    fun onbtnStart(view: View) {
        dados.mudaNomeEquipa(nomeEquipa.text.toString())
        val intent = Intent(this, Jogo::class.java)
        startActivity(intent)
        finish()
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Servidor::class.java)
        startActivity(intent)
        finish()
    }


}