package com.evanemran.wolclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_ping.setOnClickListener {
            var p : Ping = ping(URL(editText_hostName.text.toString()), this)
            Toast.makeText(this, p.getResponse(), Toast.LENGTH_LONG).show()
        }
    }
}