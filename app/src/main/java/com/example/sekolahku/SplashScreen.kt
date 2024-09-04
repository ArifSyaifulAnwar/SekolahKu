package com.example.sekolahku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val splashTimer = 2000
        val mainIntent = Intent(this, Pilihan::class.java) // Change MainActivity with your main activity
        val splashTimerTask = object : Thread() {
            override fun run() {
                try {
                    sleep(splashTimer.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(mainIntent)
                    finish()


                }
            }
        }
        splashTimerTask.start()
    }
}