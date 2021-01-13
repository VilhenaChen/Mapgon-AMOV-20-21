package pt.vilhena.mapgon.logica
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class Dados : Serializable {

    private var arrayJogadores = ArrayList<Jogador>()
    private lateinit var nomeEquipa : String
    private lateinit var idEquipa : String

    fun adicionaJogador(latitude : String, longitude : String)
    {
        if(arrayJogadores.isEmpty()){
            arrayJogadores.add(Jogador(1, latitude, longitude))
        }
        else{
            arrayJogadores.add(Jogador((arrayJogadores.last().id + 1), latitude, longitude))
        }
    }

    //Define o nome da equipa (idEquipa - nomeEscolhido)
    fun mudaNomeEquipa(nome : String) {
        nomeEquipa = "$idEquipa - $nome"
    }

    //Insere um jogador na Base de Dados
    fun inserJogadorDB(jogador : String, latitude : String, longitude : String) {
        val db = Firebase.firestore
        val coordenadas = hashMapOf(
            "Latitude" to latitude,
            "Longitude" to longitude
        )
        db.collection("Equipas").document(nomeEquipa).collection(jogador).document("coordenadas").set(coordenadas)
    }

    //Atualiza a seccao da base de dados relativa ao jogador passado
    fun atualizaDB(jogador : String, latitude : String, longitude : String) {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa).collection(jogador).document("coordenadas")

        //.get vai buscar a informcao a Base de Dados, so depois e que se altera
        /*v.get(Source.SERVER)
            .addOnSuccessListener {
                v.update("Latitude",latitude,"longitude",longitude)
            }
        */
        //Esta versao ajuda caso haja muitas threads a fazer varias coisas ao msm tempo na bd
        db.runTransaction { transition ->
            //val doc = transition.get(v)
            val latitudeatual = latitude
            val longitudeatual = longitude
            transition.update(v,"Latitude",latitudeatual)
            transition.update(v,"Longitude",longitudeatual)
            null
        }
    }

    //Le da base de Dados a lista de poligonos para depois mostarar na scoreboard
    fun getListaPoligonos() {

    }

    //Le da base de Dados o Top de Equipas de um certo poligono
    fun getTopEquipas(poligono : String) {

    }
}