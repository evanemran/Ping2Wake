package com.evanemran.wolclient

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.Nullable
import java.net.InetAddress
import java.net.Socket
import java.net.URL


class Ping {
   var net = "NO_CONNECTION"
   var host = ""
   var ip = ""
   var dns = Int.MAX_VALUE
   var cnt = Int.MAX_VALUE

   fun getResponse(): String {
      val response = "CNT: " + cnt +" DNS: " + dns + " Host: " + host + " Connection: " + net
      return response
   }
}

fun ping(url: URL, ctx: Context?): Ping {
   val r = Ping()
   if (isNetworkConnected(ctx!!)) {
      r.net = getNetworkType(ctx)
      try {
         val hostAddress: String
         val start = System.currentTimeMillis()
         hostAddress = InetAddress.getByName(url.getHost()).getHostAddress()
         val dnsResolved = System.currentTimeMillis()
         val socket = Socket(hostAddress, url.getPort())
         socket.close()
         val probeFinish = System.currentTimeMillis()
         r.dns = (dnsResolved - start).toInt()
         r.cnt = (probeFinish - dnsResolved).toInt()
         r.host = url.getHost()
         r.ip = hostAddress
      } catch (ex: Exception) {
         Log.e("error", "Unable to ping")
      }
   }
   return r
}
fun isNetworkConnected(context: Context): Boolean {
   val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
   val activeNetwork = cm.activeNetworkInfo
   return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

@Nullable
fun getNetworkType(context: Context): String {
   val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
   val activeNetwork = cm.activeNetworkInfo
   return activeNetwork?.typeName.toString()
}