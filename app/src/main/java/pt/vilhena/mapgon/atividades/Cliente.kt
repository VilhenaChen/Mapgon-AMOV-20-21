package pt.vilhena.mapgon.atividades

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cliente.*
import kotlinx.android.synthetic.main.activity_servidor.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.ModeloVistaJogo
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.SERVER_PORT
import pt.vilhena.mapgon.logica.Dados

class Cliente : AppCompatActivity()  {

    private lateinit var model : ModeloVistaJogo
    private var dlg : AlertDialog? = null
    lateinit var dados : Dados
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude : String = ""
    var longitude : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente)

        dados = intent.getSerializableExtra("Dados") as Dados



        /*model = ViewModelProvider(this).get(ModeloVistaJogo::class.java)
        model.connectionState.observe(this) {
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
            startAsClient()
        }*/
    }

    private fun startAsClient() {
        val edtBox = EditText(this).apply {
            maxLines = 1
            filters = arrayOf(object : InputFilter {
                override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                ): CharSequence? {
                    if (source?.none { it.isDigit() || it == '.' } == true)
                        return ""
                    return null
                }

            })
        }
        val dlg = androidx.appcompat.app.AlertDialog.Builder(this).run {
            setTitle(getString(R.string.Modo_Cliente))
            setMessage(getString(R.string.InsiraIP))
            setPositiveButton(getString(R.string.Conectar)) { _: DialogInterface, _: Int ->
                val strIP = edtBox.text.toString()
                if (strIP.isEmpty() || !Patterns.IP_ADDRESS.matcher(strIP).matches()) {
                    Toast.makeText(this@Cliente, getString(R.string.error_address), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    model.startClient(edtBox.text.toString())
                }
            }
            setNeutralButton(getString(R.string.btn_emulator)) { _: DialogInterface, _: Int ->
                model.startClient("10.0.2.2", SERVER_PORT-1)
                // Add port redirect on the Server Emulator:
                // telnet localhost <5554|5556|5558|...>
                // auth <key>
                // redir add tcp:9998:9999
            }
            setNegativeButton(getString(R.string.button_cancel)) { _: DialogInterface, _: Int ->
                finish()
            }
            setCancelable(false)
            setView(edtBox)
            create()
        }
        dlg.show()
    }



    //Funcao do Botao "connectar"
    fun onConnect(view: View) {
        val db = Firebase.firestore
        if(!IPClient.text.isEmpty()) {
            db.collection("Equipas").document(IPClient.text.toString()).get().addOnSuccessListener { v ->
                if(v.exists()){ //Caso a equipa exista
                    Log.d("AQUI","POIS")
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    if(permission == PackageManager.PERMISSION_GRANTED) {
                        Log.d("AQUI 1/5","POIS")
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                Log.d("AQUI 2","POIS")
                                latitude = location!!.latitude.toString()
                                Log.d("AQUI 2.1","POIS")
                                longitude = location!!.longitude.toString()
                                Log.d("AQUI 2.2","POIS")
                                dados.setIDEquipa(IPClient.text.toString())
                                Log.d("AQUI 2.3","POIS")
                                dados.mudaNomeEquipa("")
                                Log.d("AQUI 2.4","POIS")
                                dados.getInfoEquipa()
                                Log.d("AQUI 3","POIS")
                                Log.d("OI",dados.getArrayJogadores().last().latitude.toDouble().toString())
                                var dist = dados.getFuncoesCoordenadas().haversine(dados.getArrayJogadores().last().latitude.toDouble(),dados.getArrayJogadores().last().longitude.toDouble(),dados.getArrayJogadores()[0].latitude.toDouble(),dados.getArrayJogadores()[0].longitude.toDouble())
                                Log.d("PUTA QUE ME PARIU", dist.toString())
                                Log.d("AQUI 4","POIS")
                                if(dist <= 0.1){
                                    Log.d("AQUI 5","POIS")
                                    dados.adicionaJogador(latitude, longitude)
                                    dados.insereJogadorDB()
                                    val intent = Intent(this, Jogo::class.java)
                                    intent.putExtra("Dados", dados)
                                    startActivity(intent)
                                    finish()  
                                }
                                else {
                                    Toast.makeText(this, "Tem de estar a uma distanca maxima de 100m do Servidor", Toast.LENGTH_SHORT).show()
                                    return@addOnSuccessListener
                                }
                            }
                            else {
                                Log.d("FIM", "POIS")
                            }
                        }
                    }
                }
                else { //Caso a equipa nao exista na Base de Dados
                    Toast.makeText(this, "O Servidor nao existe", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
            }
        }
        else
        {
            Toast.makeText(this, "O campo do IP nao pode estar vazio", Toast.LENGTH_SHORT).show()
            return
        }
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