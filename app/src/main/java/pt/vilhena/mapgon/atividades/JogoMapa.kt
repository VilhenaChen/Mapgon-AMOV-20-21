package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados

class JogoMapa : AppCompatActivity()  {
    lateinit var dados : Dados
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jogo_mapa)

        dados = intent.getSerializableExtra("Dados") as Dados
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        intent.putExtra("Dados", dados)
        val intent = Intent(this, Jogo::class.java)
        startActivity(intent)
        finish()
    }
}