package com.elmohands.engine_agency_clients

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.elmohands.engine_agency_clients.ui.AdminHome
import com.elmohands.engine_agency_clients.ui.Login
import com.elmohands.engine_agency_clients.ui.UserHome


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences("engine_agency_clients", MODE_PRIVATE)
        val login: Boolean = sharedPreferences!!.getBoolean("login", false)
        val intent = if(login) {
            if (sharedPreferences.getString("type", "").equals("user"))
                Intent(this, UserHome::class.java)
            else
                Intent(this, AdminHome::class.java)
        }else
            Intent(this, Login::class.java)

        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(1000)
                    startActivity(intent)
                    finish()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        t.start()
    }

}