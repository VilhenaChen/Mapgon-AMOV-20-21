package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R

class TopJogadores : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_jogadores)
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Poligonos::class.java)
        startActivity(intent)
        finish()
    }
}