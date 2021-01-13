package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.ModeloVistaJogo
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.SERVER_PORT
import pt.vilhena.mapgon.logica.Dados

class Cliente : AppCompatActivity()  {

    private lateinit var model : ModeloVistaJogo
    private var dlg : AlertDialog? = null
    lateinit var dados : Dados
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

    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }

    fun onConnect(view: View) {
        val intent = Intent(this, Jogo::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }
}