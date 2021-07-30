package com.example.firebaseappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
    fun proceed(v: View?)
    {
        var i= Intent(applicationContext,LoginActivity::class.java)
        startActivity(i)

    }
}