package pt.vilhena.mapgon.logica

import java.io.Serializable

class FuncoesCoordenadas : Serializable {

    private val raioTerra = 6371

    fun haversine(latCliente : Double, longCliente: Double, latServidor : Double, longServidor: Double) : Double {
        val distLat = Math.toRadians(latCliente - latServidor);
        val distLong = Math.toRadians(longCliente - longServidor);
        val latServidorRad = Math.toRadians(latServidor);
        val latClienteRad = Math.toRadians(latCliente);

        val a = Math.pow(Math.sin(distLat / 2), 2.toDouble()) + Math.pow(Math.sin(distLong / 2), 2.toDouble()) * Math.cos(latServidorRad) * Math.cos(latClienteRad);
        val c = 2 * Math.asin(Math.sqrt(a));
        return raioTerra * c;
    }
}