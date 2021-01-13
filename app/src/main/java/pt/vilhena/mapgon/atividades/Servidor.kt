
package pt.vilhena.mapgon.atividades

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_servidor.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.ModeloVistaJogo
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.SERVER_PORT
import pt.vilhena.mapgon.logica.Dados


const val SERVER_MODE = 0
const val CLIENT_MODE = 1

class Servidor : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val request = LocationRequest()
    var latitude : String = ""
    var longitude : String = ""
    var coordenadas : String =""
    lateinit var dados : Dados

    //private lateinit var model : ModeloVistaJogo
    //private var dlg : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servidor)
        dados = intent.getSerializableExtra("Dados") as Dados
        //model = ViewModelProvider(this).get(ModeloVistaJogo::class.java)

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
        /*model.connectionState.observe(this) {
            if (it != ModeloVistaJogo.ConnectionState.SETTING_PARAMETERS &&
                    it != ModeloVistaJogo.ConnectionState.SERVER_CONNECTING && dlg?.isShowing == true) {
                dlg?.dismiss()
                dlg = null
            }

            if (it == ModeloVistaJogo.ConnectionState.CONNECTION_ERROR ||
                    it == ModeloVistaJogo.ConnectionState.CONNECTION_ENDED)
                finish()
        }
        if (model.connectionState.value != ModeloVistaJogo.ConnectionState.CONNECTION_ESTABLISHED) {
                startAsServer()
        }*/
    }

    /*
    override fun onPause() {
        super.onPause()
        dlg?.apply {
            if (isShowing)
                dismiss()
        }
    }
    */
    /*
    private fun startAsServer() {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = wifiManager.connectionInfo.ipAddress
        val strIPAddress = String.format("%d.%d.%d.%d",
                ip and 0xff,
                (ip shr 8) and 0xff,
                (ip shr 16) and 0xff,
                (ip shr 24) and 0xff
        )

        val ll = LinearLayout(this).apply {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            this.setPadding(50, 50, 50, 50)
            layoutParams = params
            setBackgroundColor(Color.rgb(240, 224, 208))
            orientation = LinearLayout.HORIZONTAL
            addView(ProgressBar(context).apply {
                isIndeterminate = true
                val paramsPB = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                paramsPB.gravity = Gravity.CENTER_VERTICAL
                layoutParams = paramsPB
                indeterminateTintList = ColorStateList.valueOf(Color.rgb(96, 96, 32))
            })
            addView(TextView(context).apply {
                val paramsTV = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams = paramsTV
                text = String.format(getString(R.string.msg_ip_address),strIPAddress)
                textSize = 20f
                setTextColor(Color.rgb(96, 96, 32))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            })
        }

        dlg = AlertDialog.Builder(this).run {
            setTitle(getString(R.string.Modo_Servidor))
            setView(ll)
            setOnCancelListener {
                model.stopServer()
                finish()
            }
            create()
        }
        model.startServer()

        dlg?.show()
    } */

    //Escrever na Firesbase as coordenadas
    fun onCloseTeam(view: View) {
        val intent = Intent(this, DefineEquipa::class.java)
        intent.putExtra("Dados", dados);
        startActivity(intent)
        finish()
    }

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }

}

