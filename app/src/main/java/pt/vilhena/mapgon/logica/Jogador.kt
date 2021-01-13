package pt.vilhena.mapgon.logica

import java.io.Serializable

class Jogador : Serializable {
    var id : Int = 0
    var nome : String = ""
    var latitude : String = ""
    var longitude : String = ""

    constructor(id:Int, latitude : String, longitude : String){
        this.id = id
        this.nome = "Jogador$id"
        this.latitude = latitude
        this.longitude = longitude
    }
}