package pt.vilhena.mapgon.logica

import java.io.Serializable

class Jogador : Serializable {
    var id : Int = 0
        private set(value) {
            field = value
        }
    var nome : String = ""
        private set(value) {
            field = value
        }
    var latitude : String = ""
        private set(value) {
            field = value
        }
    var longitude : String = ""
        private set(value) {
            field = value
        }

    constructor(id:Int, latitude : String, longitude : String){
        this.id = id
        this.nome = "Jogador$id"
        this.latitude = latitude
        this.longitude = longitude
    }


}