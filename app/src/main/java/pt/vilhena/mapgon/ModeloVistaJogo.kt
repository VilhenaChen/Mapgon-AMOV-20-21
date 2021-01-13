package pt.vilhena.mapgon

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

const val SERVER_PORT = 9999


class ModeloVistaJogo : ViewModel(){


    enum class ConnectionState
    {
        SETTING_PARAMETERS, SERVER_CONNECTING, CLIENT_CONNECTING, CONNECTION_ESTABLISHED, CONNECTION_ERROR, CONNECTION_ENDED
    }

    val connectionState = MutableLiveData(ConnectionState.SETTING_PARAMETERS)


   
    private var socket : Socket? = null
    private val socketI : InputStream?
        get() = socket?.getInputStream()
    private val socketO : OutputStream?
        get() = socket?.getOutputStream()

    private var serverSocket : ServerSocket? = null

    private var threadComm : Thread? = null

    fun startServer()
    {
        if(serverSocket != null || socket != null || connectionState.value != ConnectionState.SETTING_PARAMETERS)
            return
        connectionState.postValue(ConnectionState.SERVER_CONNECTING)
        thread {
            serverSocket = ServerSocket(SERVER_PORT)
            serverSocket?.apply{
                try{
                    startComm(serverSocket!!.accept())
                }catch (_: Exception){
                    connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                }finally {
                    serverSocket?.close()
                    serverSocket = null
                }
            }
        }
    }

    fun stopServer() {
        serverSocket?.close()
        connectionState.postValue(ConnectionState.CONNECTION_ENDED)
        serverSocket = null
    }

    fun startClient(serverIP: String, serverPort: Int = SERVER_PORT){
        if(socket != null || connectionState.value != ConnectionState.SETTING_PARAMETERS)
            return
        thread{
            connectionState.postValue(ConnectionState.CLIENT_CONNECTING)
            try{
                val newsocket = Socket(serverIP, serverPort)
                startComm(newsocket)
            }catch (_: Exception){
                connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            }
        }
    }

    fun startComm(newSocket: Socket){
        if(threadComm != null)
            return
        socket = newSocket
        threadComm = thread {
            try {
                if (socketI == null)
                    return@thread
                connectionState.postValue(ConnectionState.CONNECTION_ESTABLISHED)
                //val bufI = socketI!!.bufferedReader()
            }catch (_: Exception){
            }finally {
                stopGame()
            }
        }
    }

    fun stopGame(){
        try {
            connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            socket?.close()
            socket = null
            threadComm?.interrupt()
            threadComm = null
        }catch (_: Exception){}
    }
}