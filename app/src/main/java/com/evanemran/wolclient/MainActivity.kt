package com.evanemran.wolclient

import android.os.Bundle
import android.util.Log
import android.view.View.Z
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException
import java.net.*

class MainActivity : AppCompatActivity() {
    var ip = ""
    var mac = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_ping.setOnClickListener {
            ping(ip)
//            try{
//                val p : Ping = ping(URL(editText_hostName.text.toString()), this)
//                Snackbar.make(findViewById(android.R.id.content), p.getResponse(), Snackbar.LENGTH_SHORT).show()
//            }
//            catch (e: Exception) {
//                Snackbar.make(findViewById(android.R.id.content), e.message.toString(), Snackbar.LENGTH_SHORT).show()
//            }
        }

        button_wol.setOnClickListener {
            ip = editText_hostName.text.toString()
            mac = editText_mac.text.toString()
            wakeUp("http://$ip", mac)
        }
    }

    private fun wakeUp(broadcastIP: String?, mac: String?) {
        Log.d("wakeup", "method started")
        if (mac == null) {
            Log.d("wakeup", "mac = null")
            return
        }
        try {
            val macBytes: ByteArray = getMacBytes(mac)
            Log.d("wakeup", String(macBytes))
            val bytes = ByteArray(6 + 16 * macBytes.size)
            for (i in 0..5) {
                bytes[i] = 0xff.toByte()
            }
            var i = 6
            while (i < bytes.size) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.size)
                i += macBytes.size
            }
            Log.d("wakeup", "calculating completed, sending...")
            val address: InetAddress = InetAddress.getByName(broadcastIP)
            val packet = DatagramPacket(bytes, bytes.size, address, 9)
            val socket = DatagramSocket()
            socket.send(packet)
            socket.close()
            Log.d("wakeup", "Magic Packet sent")
            Snackbar.make(findViewById(android.R.id.content), "Magic Packet sent", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("wakeup", e.printStackTrace().toString())
            Snackbar.make(findViewById(android.R.id.content), e.printStackTrace().toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun getMacBytes(mac: String): ByteArray {
        Log.d("GetMacBytes", "method started")
        // TODO Auto-generated method stub
        val bytes = ByteArray(6)
        try {
            var hex: String
            for (i in 0..5) {
                hex = mac.substring(i * 2, i * 2 + 2)
                bytes[i] = hex.toInt(16).toByte()
                Log.d("GetMacbytes", "calculated")
                Log.d("GetMacBytes (bytes)", String(bytes))
            }
        } catch (e: Exception) {
            Log.e("GetMacBytes", e.message.toString())
        }
        return bytes
    }


    fun ping(ip: String) {
        try {
            val url = URL("http://$ip")
            val urlc: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Android Application:"/* + Z.APP_VERSION*/)
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 1000 * 30 // Timeout is in seconds
            urlc.connect()
            if (urlc.responseCode == 200) {
                Snackbar.make(findViewById(android.R.id.content), urlc.responseMessage, Snackbar.LENGTH_SHORT).show()
                return
            }
        } catch (e1: MalformedURLException) {
            e1.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}