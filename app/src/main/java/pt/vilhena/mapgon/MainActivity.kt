package pt.vilhena.mapgon

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pt.vilhena.mapgon.atividades.Cliente
import pt.vilhena.mapgon.atividades.Poligonos
import pt.vilhena.mapgon.atividades.Servidor
import pt.vilhena.mapgon.atividades.Sobre

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 2)
        }
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