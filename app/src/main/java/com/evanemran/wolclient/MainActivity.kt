package com.evanemran.wolclient

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.evanemran.wolclient.adapter.SpinAdapter
import com.evanemran.wolclient.database.RoomDB
import com.evanemran.wolclient.dialog.AddDeviceDialog
import com.evanemran.wolclient.listener.AddListener
import com.evanemran.wolclient.model.Device
import com.evanemran.wolclient.packet.MagicPacket
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.*


class MainActivity : AppCompatActivity() , AddListener {
    var ip = ""
    var mac = ""
    var database: RoomDB? = null
    var deviceList: List<Device> = mutableListOf()
    lateinit var magicPacket: MagicPacket;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        magicPacket = MagicPacket(this)

        database = RoomDB.getInstance(this)
        deviceList = database?.mainDAO()!!.all

        //adapter for spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            deviceList
        )

        //custom adapter
        val cAdapter = SpinAdapter(this,
            R.layout.spinner_text,
            deviceList)


        spinner.adapter = cAdapter

        spinner.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val device = deviceList[position]
                editText_hostName.setText(device.deviceIp)
                editText_mac.setText(device.deviceMac)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        button_ping.setOnClickListener {

            var commands: MutableList<String> = mutableListOf()
            commands.add("ping")
            commands.add(editText_hostName.text.toString())

            ping(commands)


//            ping(ip)
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
            mac = "http://"+editText_mac.text.toString()
//            wakeUp("https://$ip", mac)

            Thread {
                try {
                    mac = MagicPacket.cleanMac(mac)
                    magicPacket.send(mac, ip)
                } catch (e: IllegalArgumentException) {
                    println(e.message)
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Failed to send packet " + e.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } catch (e: java.lang.Exception) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Error while sending packet " + e.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                runOnUiThread {
                    textView_console.text = textView_console.text.toString() + "\n" + "verifying mac address..."
                    textView_console.text = textView_console.text.toString() + "\n" + "mac address verified..."
                    textView_console.text = textView_console.text.toString() + "\n" + "Packet sent to $mac..."
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Packet sent to $mac",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }.start()
        }

        imageButton_add.setOnClickListener {
            val addDeviceDialog = AddDeviceDialog(this)
            addDeviceDialog.show(supportFragmentManager, "payment")
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

            val thread = Thread {
                try {
                    //Your code goes here
                    val address: InetAddress = InetAddress.getByName(broadcastIP)
                    val packet = DatagramPacket(bytes, bytes.size, address, 9)
                    val socket = DatagramSocket()
                    socket.send(packet)
                    socket.close()
                    Log.d("wakeup", "Magic Packet sent")
                    Snackbar.make(findViewById(android.R.id.content), "Magic Packet sent", Snackbar.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Snackbar.make(findViewById(android.R.id.content), e.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }

            thread.start()

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

    private fun ping(commands: List<String>) {

        Thread {
            try {
                val processBuilder: ProcessBuilder = ProcessBuilder(commands)
                val process: Process = processBuilder.start()

                val input = BufferedReader(InputStreamReader(process.inputStream))
                val Error = BufferedReader(InputStreamReader(process.errorStream))
                var s: String? = ""

                println("Standard output: ")
                while (input.readLine().also { s = it } != null) {
                    textView_console.text = textView_console.text.toString() + "\n" + s
                    System.out.println(s)
                }
                println("error (if any): ")
                while (Error.readLine().also { s = it } != null) {
                    textView_console.text = textView_console.text.toString() + "\n" + s
                    System.out.println(s)
                }
            } catch (e: Exception) {
                println(e.message)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Failed to send ping " + e.message.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            runOnUiThread {
            }
        }.start()

    }

    private fun ping(ip: String) {
        try {
            val url = URL(ip)
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
            Snackbar.make(findViewById(android.R.id.content), e1.message.toString(), Snackbar.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Snackbar.make(findViewById(android.R.id.content), e.message.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onAddClicked(device: Device) {
        database?.mainDAO()!!.insert(device)
        Snackbar.make(findViewById(android.R.id.content), device.deviceName + " added successfully!", Snackbar.LENGTH_SHORT).show()
    }
}