package com.evanemran.wolclient

import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MagicPacket(wifiManager: WifiManager) {

//    private val wifiManager: WifiManager? = null

    var executor: ExecutorService = Executors.newSingleThreadExecutor()
    var handler: Handler = Handler(Looper.getMainLooper())

    fun execute() {
        executor.execute(Runnable {
            run {
                handler.post(Runnable {
                    run {

                    }
                })
            }
        })
    }

}