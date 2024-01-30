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
import com.evanemran.wolclient.listener.PingListener
import com.evanemran.wolclient.model.Device
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.*


class MainActivity : AppCompatActivity() , AddListener {
    var ip = ""
    var mac = ""
    var port = 9
    var deviceName: String = ""
    var database: RoomDB? = null
    var deviceList: List<Device> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = RoomDB.getInstance(this)
        deviceList = database?.mainDAO()!!.all

        //custom adapter for host
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
                deviceName = device.deviceName
                editText_hostName.setText(device.deviceIp)
                editText_mac.setText(device.deviceMac)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        button_ping.setOnClickListener {
            var host = editText_hostName.text.toString()

            runOnUiThread {
                textView_console.append("\nPinging $host")
            }

            pingServer(host, object : PingListener {
                override fun onSuccess(message: String) {
                    runOnUiThread {
                        textView_console.append("\n$message")
                    }
                }

                override fun onFailure(errorMessage: String) {
                    runOnUiThread {
                        textView_console.append("\n$errorMessage")
                    }
                }

            })
        }

        button_wol.setOnClickListener {
            ip = editText_hostName.text.toString()
            mac = editText_mac.text.toString()

            sendMagicPacket(ip, mac, 9, object : PingListener{
                override fun onSuccess(message: String) {
                    runOnUiThread {
                        textView_console.append("\n$message")
                    }
                }

                override fun onFailure(errorMessage: String) {
                    runOnUiThread {
                        textView_console.append("\n$errorMessage")
                    }
                }

            })
        }

        imageButton_add.setOnClickListener {
            val addDeviceDialog = AddDeviceDialog(this)
            addDeviceDialog.show(supportFragmentManager, "device")
        }
    }


    fun pingServer(serverAddress: String, listener: PingListener) {
        Thread{
            try {
                val address = InetAddress.getByName(serverAddress)

                // Timeout is set to 5000 milliseconds (5 seconds)
                val isReachable = address.isReachable(5000)

                if (isReachable) {
                    // Log success
                    listener.onSuccess("Ping - Server $serverAddress is reachable.")
                } else {
                    // Log failure
                    listener.onFailure("Ping - Server $serverAddress is not reachable.")
                }

            } catch (e: IOException) {
                // Log exception
                listener.onSuccess("Ping - Error during ping: ${e.message}")
            }
        }.start()
    }

    private fun sendMagicPacket(ip: String, mac: String, port: Int, listener: PingListener) {
        Thread {
            try {
                val macBytes = mac.split(":").map { it.toInt(16).toByte() }.toByteArray()

                listener.onSuccess("Verifying MAC address.")

                // Create the magic packet
                val magicPacket = ByteArray(6 + 16 * macBytes.size)
                for (i in 0 until 6) {
                    magicPacket[i] = 0xFF.toByte()
                }
                for (i in 6 until magicPacket.size step macBytes.size) {
                    System.arraycopy(macBytes, 0, magicPacket, i, macBytes.size)
                }

                // Create a DatagramSocket and send the magic packet
                val socket = DatagramSocket()
                val address = InetAddress.getByName(ip)
                val packet = DatagramPacket(magicPacket, magicPacket.size, address, port)
                socket.send(packet)

                // Close the socket
                socket.close()

                listener.onSuccess("Packet Sent Successfully! Turning on Device...")

            } catch (e: Exception) {
                // Handle exceptions, such as SocketException or UnknownHostException
                listener.onFailure("Couldn't send a wake-up packet. Check the connection")
                e.printStackTrace()
            }
        }.start()
    }

    override fun onAddClicked(device: Device) {
        database?.mainDAO()!!.insert(device)
        Snackbar.make(findViewById(android.R.id.content), device.deviceName + " added successfully!", Snackbar.LENGTH_SHORT).show()
    }
}