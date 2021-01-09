package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R

class Sobre : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre)
    }


    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}