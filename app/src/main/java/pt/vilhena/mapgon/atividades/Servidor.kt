package pt.vilhena.mapgon.atividades

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_servidor.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R

class Servidor : Activity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val request = LocationRequest()
    var latitude : String = ""
    var longitude : String = ""
    var coordenadas : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servidor)

        request.interval=1000
        request.fastestInterval = 500
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permission =ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationClient.requestLocationUpdates(request, object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    val location : Location? = locationResult.lastLocation
                    if(location!=null)
                    {
                        latitude = location!!.latitude.toString()
                        longitude = location!!.longitude.toString()
                        coordenadas = latitude + " ; " + longitude
                        coordenadasIniciais.text = coordenadas
                    }
                }
            },null)
        }

    }


    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

