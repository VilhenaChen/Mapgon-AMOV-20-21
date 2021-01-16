package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados

class AguardaJogo : AppCompatActivity()  {
    private val mainscope = MainScope()
    lateinit var dados : Dados


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aguarda_jogo)
        dados = intent.getSerializableExtra("Dados") as Dados

        mainscope.launch(Dispatchers.Default){
            val db = Firebase.firestore
            var flag : Boolean = false
            val c = db.collection("Equipas").document(dados.nomeEquipa)
            while(true) {
                db.runTransaction { transation ->
                    val doc = transation.get(c)
                    flag = doc.getBoolean("Comecou")!!
                    if (flag) {
                        comecaJogo()
                    }
                    null
                }
                delay(1000)
            }
        }
    }

    fun comecaJogo(){
        val intent = Intent(this, Jogo::class.java)
        intent.putExtra("Dados", dados)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainscope.cancel()
    }


}
