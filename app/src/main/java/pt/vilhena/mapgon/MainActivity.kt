package pt.vilhena.mapgon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import pt.vilhena.mapgon.atividades.Cliente
import pt.vilhena.mapgon.atividades.Poligonos
import pt.vilhena.mapgon.atividades.Servidor
import pt.vilhena.mapgon.atividades.Sobre

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onServerMode(view: View) {
        val intent = Intent(this, Servidor::class.java)
        startActivity(intent)
        finish()
    }
    fun onClientMode(view: View) {
        val intent = Intent(this, Cliente::class.java)
        startActivity(intent)
        finish()
    }
    fun onScoreboard(view: View) {
        val intent = Intent(this, Poligonos::class.java)
        startActivity(intent)
        finish()
    }
    fun onAbout(view: View) {
        val intent = Intent(this, Sobre::class.java)
        startActivity(intent)
        finish()
    }
}