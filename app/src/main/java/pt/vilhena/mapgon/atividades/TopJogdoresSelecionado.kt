package pt.vilhena.mapgon.atividades

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_top_jogdores_selecionado.*
import pt.vilhena.mapgon.MainActivity
import pt.vilhena.mapgon.R
import pt.vilhena.mapgon.logica.Dados

class TopJogdoresSelecionado : AppCompatActivity()  {
    lateinit var dados : Dados
    lateinit var poli : String
    lateinit var equipa : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_jogdores_selecionado)

        dados = intent.getSerializableExtra("dados")!! as Dados
        poli = intent.getStringExtra("Poligono")!!
        equipa = intent.getStringExtra("Equipa")!!

        val db = Firebase.firestore
        var idTeam = ""
        var data = ""
        var hora = ""
        var dist = ""
        var area = ""
        val c = db.collection("Poligonos").document("Decagono").collection("BueDaNice").document("Dados")
        db.runTransaction { transation ->
            val doc = transation.get(c)
            idTeam = doc.getString("ID")!!
            data = doc.getString("Data")!!
            hora = doc.getString("Hora")!!
            dist = doc.getString("Distancia")!!
            area = doc.getString("Area")!!
            null
        }
        while (area.isEmpty()){}
        poligonoEquipaSelecionada.text = poli
        nomeEquipaTOP.text = equipa
        idEquipaTop.text = idTeam
        dataTop.text = data
        horaTop.text = hora
        DistanciaMedia.text = dist
        areaPoli.text = area
    }


    //Voltar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Dados", dados);
        startActivity(intent)
        finish()
    }
}