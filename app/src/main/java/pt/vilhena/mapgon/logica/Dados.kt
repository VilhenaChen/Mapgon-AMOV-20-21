package pt.vilhena.mapgon.logica
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_define_equipa.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Dados : Serializable {

    private val funcoesCoordenadas = FuncoesCoordenadas()
    private var arrayJogadores = ArrayList<Jogador>()
    var nomeEquipa : String = ""
        private set(value) {
            field = value
        }
    private lateinit var nomeEquipaDB : String
    private lateinit var idEquipa : String
    private var arrayPoligonos : ArrayList<String> = arrayListOf("Triangulo", "Quadrado", "Pentagono", "Hexagono", "Heptagono", "Octogono", "Eneagono", "Decagono")
    var poli = ""
    var idProprio = 11

    //get Funcoes Coordenadas
    fun getFuncoesCoordenadas() : FuncoesCoordenadas{
        return funcoesCoordenadas
    }
    //get Array Jogadores
    fun getArrayJogadores() : ArrayList<Jogador>{
        return arrayJogadores
    }

    //get Array Poligonos
    fun getPoligonos() : ArrayList<String>{
        return arrayPoligonos
    }

    //Adiciona Jogador ao Array de Jogadores
    fun adicionaJogador(latitude: String, longitude: String)
    {
        if(arrayJogadores.isEmpty()){
            arrayJogadores.add(Jogador(1, latitude, longitude))
        }
        else{
            arrayJogadores.add(Jogador((arrayJogadores.last().id + 1), latitude, longitude))
        }
    }

    //Define o Id da equipa
    fun setIDEquipa(id: String) {
        idEquipa = id
    }

    //Gera o Id da Equipa  latitude|longitude|NrJogadores|Data|Hora
    fun geraIDEquipa() {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa)
        var id = ""
        var lat = 0.00
        var latInt = 0
        var long = 0.00
        var longInt = 0
        lat =arrayJogadores[0].latitude.toDouble()
        long =arrayJogadores[0].longitude.toDouble()
        lat *= 100
        latInt = lat.toInt()
        lat = latInt.toDouble()
        lat /= 100
        long *= 100
        longInt = long.toInt()
        long = longInt.toDouble()
        long /= 100
        val sdf = SimpleDateFormat("ddMMyyyy|HHmm")
        var dataEHora = sdf.format(Date())
        id = "$lat|$long|${arrayJogadores.size}|$dataEHora"
        db.runTransaction { transition ->
            transition.update(v, "IDEquipa", id)
            null
        }


    }

    //Define o nome da equipa (idEquipa - nomeEscolhido)
    fun mudaNomeEquipa(nome: String) {
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
                "Longitude" to arrayJogadores[0].longitude,
                "Acabou" to false
        )
        val jogo = hashMapOf(
                "nrJogadores" to 1,
                "Comecou" to false,
                "IDEquipa" to "",
                "NomeEquipa" to "",
                "Comprimento Medio da Aresta" to 0,
                "Area do Poligono" to 0,
                "Poligono" to "",
                "Acabaram Todos" to false
        )
        db.collection("Equipas").document(nomeEquipa).collection(arrayJogadores[0].nome).document("coordenadas").set(coordenadas)
        db.collection("Equipas").document(nomeEquipa).set(jogo)
    }

    //Insere um jogador na Base de Dados
    fun insereJogadorDB() {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa)
        val coordenadas = hashMapOf(
                "Latitude" to arrayJogadores.last().latitude,
                "Longitude" to arrayJogadores.last().longitude,
                "Acabou" to false
        )
        db.collection("Equipas").document(nomeEquipa).collection(arrayJogadores.last().nome).document("coordenadas").set(coordenadas)

        db.runTransaction { transition ->
            val doc = transition.get(v)
            val numero =  doc.getLong("nrJogadores")!! + 1
            transition.update(v, "nrJogadores", numero)
            null
        }
    }

    //Insere um jogador na Base de Dados
    fun insereJogadorEspecificoDB(pos: Int) {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa)
        val coordenadas = hashMapOf(
                "Latitude" to arrayJogadores[pos].latitude,
                "Longitude" to arrayJogadores[pos].longitude,
                "Acabou" to false
        )
        db.collection("Equipas").document(nomeEquipa).collection(arrayJogadores[pos].nome).document("coordenadas").set(coordenadas)

        db.runTransaction { transition ->
            val doc = transition.get(v)
            val numero =  doc.getLong("nrJogadores")!! + 1
            transition.update(v, "nrJogadores", numero)
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
        arrayJogadores.clear()
        db.runTransaction { transition ->
            val doc = transition.get(c)
            poli = doc.getString("Poligono")!!
            null
        }
        db.runTransaction { transition ->
            val doc = transition.get(c)
            cont = doc.getLong("nrJogadores")!!.toInt()
            null
        }
        /*if(idPlayer <= cont) {

        }*/
        while(cont == 0){Log.d("LOOP", "ESPERA CONTADOR")}
        while (idPlayer <= cont)
        {
            playerName = "Jogador$idPlayer"
            val v = db.collection("Equipas").document(nomeEquipa).collection(playerName).document("coordenadas")
            db.runTransaction { transition ->
                val doc = transition.get(v)
                val latitude = doc.getString("Latitude")!!
                val longitude = doc.getString("Longitude")!!
                adicionaJogador(latitude, longitude)
                null
            }
            while(arrayJogadores.isEmpty() || arrayJogadores.size == idPlayer -1){}
            idPlayer = idPlayer + 1
        }

    }

    //Determina que tipo de Poligono sera feito, consoante o numero de players
    fun determinaPoligono() {
        var nrjogadores = 0
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa)
        db.runTransaction { transition ->
            val doc = transition.get(v)
            nrjogadores = doc.getLong("nrJogadores")!!.toInt()
            null
        }
        while(nrjogadores == 0) {}
        poli = arrayPoligonos[nrjogadores - 3]
        db.runTransaction { transition ->
            transition.update(v, "Poligono", poli)
            null
        }
    }

    //Verifica se o poligono esta completo
    fun verificaPoligono() : Boolean{
        var lado : Double = 0.0
        for(i in arrayJogadores.indices) {
            if(i == 0) {
                lado = funcoesCoordenadas.haversine(arrayJogadores[i].latitude.toDouble(), arrayJogadores[i].longitude.toDouble(), arrayJogadores[i + 1].latitude.toDouble(), arrayJogadores[i + 1].longitude.toDouble())
            } else{
                if (i == arrayJogadores.size - 1) {
                    if(lado !=  funcoesCoordenadas.haversine(arrayJogadores[i].latitude.toDouble(), arrayJogadores[i].longitude.toDouble(), arrayJogadores[0].latitude.toDouble(), arrayJogadores[0].longitude.toDouble())) {

                        return false
                    }
                }else {
                    if(lado !=  funcoesCoordenadas.haversine(arrayJogadores[i].latitude.toDouble(), arrayJogadores[i].longitude.toDouble(), arrayJogadores[i + 1].latitude.toDouble(), arrayJogadores[i + 1].longitude.toDouble())) {
                        return false
                    }
                }
            }
        }
        return true
    }

    //Vai a DB buscar o tipo de poligono
    fun getPoligonoDB() {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa)
        db.runTransaction { transition ->
            val doc = transition.get(v)
            poli = doc.getString("Poligono")!!
            null
        }
        while(poli == "") {}
    }

    //Atualiza a seccao da base de dados relativa ao jogador passado
    fun atualizaDB(jogador: String, latitude: String, longitude: String) {
        val db = Firebase.firestore
        val v = db.collection("Equipas").document(nomeEquipa).collection(jogador).document("coordenadas")


        //Esta versao ajuda caso haja muitas threads a fazer varias coisas ao msm tempo na bd
        db.runTransaction { transition ->
            //val doc = transition.get(v)
            val latitudeatual = latitude
            val longitudeatual = longitude
            transition.update(v, "Latitude", latitudeatual)
            transition.update(v, "Longitude", longitudeatual)
            null
        }
    }

    //calculaDistanciaMedia
    fun calculaDistanciaMedia() : Double{
        var dist : Double = 0.0
        dist = dist + funcoesCoordenadas.haversine(arrayJogadores[0].latitude.toDouble(), arrayJogadores[0].longitude.toDouble(), arrayJogadores[1].latitude.toDouble(), arrayJogadores[1].longitude.toDouble())
        dist *= 1000
        return dist
    }

    fun areaPoligono() : Double{
        var dist : Double = 0.0
        dist = dist + funcoesCoordenadas.haversine(arrayJogadores[0].latitude.toDouble(), arrayJogadores[0].longitude.toDouble(), arrayJogadores[1].latitude.toDouble(), arrayJogadores[1].longitude.toDouble())
        dist *= 1000
        var nLados : Double = arrayJogadores.size.toDouble()
        var area : Double = (dist * dist * nLados) / (4*Math.tan(180/nLados) * 3.14159 / 180)
        return area
    }

    //Le da base de Dados a lista de poligonos para depois mostarar na scoreboard
    fun getListaPoligonos() {
        val db = Firebase.firestore
        val c = db.collection("Poligonos")
        //Acabar
    }

    //Le da base de Dados o Top de Equipas de um certo poligono
    fun getTopEquipas(poligono: String) {
        val db = Firebase.firestore
        val c = db.collection("Poligonos").document(poligono)
        //Acabar
    }
}