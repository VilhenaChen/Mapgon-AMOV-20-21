package pt.vilhena.mapgon.logica

import android.util.Log
import java.io.Serializable

class FuncoesCoordenadas : Serializable {

    private val raioTerra = 6371

    fun haversine(lat1 : Double, long1: Double, lat2 : Double, long2: Double) : Double {

        val distLat = Math.toRadians(lat1 - lat2);
        val distLong = Math.toRadians(long1 - long2);

        val lat2Rad = Math.toRadians(lat2);
        val lat1Rad = Math.toRadians(lat1);


        val a = Math.pow(Math.sin(distLat / 2), 2.toDouble()) + Math.pow(Math.sin(distLong / 2), 2.toDouble()) * Math.cos(lat2Rad) * Math.cos(lat1Rad);

        val c = 2 * Math.asin(Math.sqrt(a));

        return raioTerra * c;
    }

    fun CalculaAngulo(lat1 : Double, long1 : Double, lat2 : Double, long2: Double, lat3 : Double, long3 : Double) : Double{
        var angulo1 = Math.atan2(long1-long2, lat1 - lat2)
        var angulo2 = Math.atan2(long2-long3, lat2-lat3)
        var anguloCalculado = Math.toDegrees(angulo1-angulo2)
        if(anguloCalculado < 0){anguloCalculado += 360}
        return anguloCalculado
    }



}