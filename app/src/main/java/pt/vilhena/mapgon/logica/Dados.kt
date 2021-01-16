package pt.vilhena.mapgon.logica
import android.util.Log
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.awaitAll
import java.io.Serializable

class Dados : Serializable {

    private val funcoesCoordenadas = FuncoesCoordenadas()
    private var arrayJogadores = ArrayList<Jogador>()
    var nomeEquipa : String = ""
        private set(value) {
            field = value
        }
    private lateinit var nomeEquipaDB : String
    private lateinit var idEquipa : String

    //get Funcoes Coordenadas
    fun getFuncoesCoordenadas() : FuncoesCoordenadas{
        return funcoesCoordenadas
    }
    //get Array Jogadores
    fun getArrayJogadores() : ArrayList<Jogador>{
        return arrayJogadores
    }

    //Adiciona Jogador ao Array de Jogadores
    fun adicionaJogador(latitude : String, longitude : String)
    {
        if(arrayJogadores.isEmpty()){
            arrayJogadores.add(Jogador(1, latitude, longitude))
        }
        else{
            arrayJogadores.add(Jogador((arrayJogadores.last().id + 1), latitude, longitude))
        }
    }

    //Define o Id da equipa
    fun setIDEquipa (id : String) {
        idEquipa = id
    }

    //Define o nome da equipa (idEquipa - nomeEscolhido)
    fun mudaNomeEquipa(nome : String) {
        if(nome == "") {
            nomeEquipa = idEquipa
        }
        else{
            nomeEquipaDB = nomeEquipa
            nomeEquipa = "$idEquipa - $nome"
        }
    }

    //Muda nome da equipa no Firebase
    fun mudaNomeEquipaDB() {
        val db = Firebase.firestore
        getInfoEquipa()
        db.collection("Equipas").document(nomeEquipaDB).delete()
        
        for (i in arrayJogadores.indices)
        {
            if(i == 0){
                criaBD()
            }
            else{
                insereJogadorEspecificoDB(i)
            }
        }
    }

    //Cria a base de Dados na firebase com o IP do server como ID da Equipa e cria a pagina do 1 jogador ou seja do player que esta a fazer de server
    fun criaBD() {
        val db = Firebase.firestore
        val coordenadas = hashMapOf(
            "Latitude" to arrayJogadores[0].latitude,
            "Longitude" to arrayJogadores[0].longitude
        )
        val jogo = hashMapOf(
            "nrJogadores" to 1,
            "Comecou" to false,
            "IDEquipa" to "",
            "NomeEquipa" to "",
            "PoligonoFeito" to false
        )
        db.collection("Equipas").document(nomeEquipa).collection( arrayJogadores[0].nome).document("coordenadas").set(coordenadas)
        db.collection("Equipas").document(nomeEquipa).set(jogo)
    }

    //Insere um jogador na Base de Dados
    fun insereJogadorDB() {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa)
        val coordenadas = hashMapOf(
            "Latitude" to arrayJogadores.last().latitude,
            "Longitude" to arrayJogadores.last().longitude
        )
        db.collection("Equipas").document(nomeEquipa).collection(arrayJogadores.last().nome).document("coordenadas").set(coordenadas)

        db.runTransaction { transition ->
            val doc = transition.get(v)
            val numero =  doc.getLong("nrJogadores")!! + 1
            transition.update(v,"nrJogadores", numero)
            null
        }
    }




    //Insere um jogador na Base de Dados
    fun insereJogadorEspecificoDB(pos : Int) {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa)
        val coordenadas = hashMapOf(
                "Latitude" to arrayJogadores[pos].latitude,
                "Longitude" to arrayJogadores[pos].longitude
        )
        db.collection("Equipas").document(nomeEquipa).collection(arrayJogadores[pos].nome).document("coordenadas").set(coordenadas)

        db.runTransaction { transition ->
            val doc = transition.get(v)
            val numero =  doc.getLong("nrJogadores")!! + 1
            transition.update(v,"nrJogadores", numero)
            null
        }
    }

    //Obtem info equipa
    fun getInfoEquipa(){
        var cont : Int = 0
        var idPlayer : Int = 1
        var playerName : String = ""
        val db = Firebase.firestore
        val c = db.collection("Equipas").document(nomeEquipa)
        Log.d("INFO","POIS")
        arrayJogadores.clear()
        Log.d("INFO 2","POIS")
        db.runTransaction { transition ->
            Log.d("INFO 3","POIS")
            val doc = transition.get(c)
            cont = doc.getLong("nrJogadores")!!.toInt()
            Log.d("INFO 4","POIS")
            null
        }
        /*if(idPlayer <= cont) {

        }*/
        while(cont == 0){Log.d("LOOP","POIS")}
        while (idPlayer <= cont)
        {
            Log.d("INFO 5","POIS")
            playerName = "Jogador$idPlayer"
            val v = db.collection("Equipas").document(nomeEquipa).collection(playerName).document("coordenadas")
            db.runTransaction { transition ->
                Log.d("INFO 6","POIS")
                val doc = transition.get(v)
                val latitude = doc.getString("Latitude")!!
                val longitude = doc.getString("Longitude")!!
                adicionaJogador(latitude,longitude)
                null
            }
            while(arrayJogadores.isEmpty() || arrayJogadores.last().id == idPlayer - 1){}
            idPlayer = idPlayer + 1
        }

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
        val db = Firebase.firestore
        val c = db.collection("Poligonos")
        //Acabar
    }

    //Le da base de Dados o Top de Equipas de um certo poligono
    fun getTopEquipas(poligono : String) {

    }
}