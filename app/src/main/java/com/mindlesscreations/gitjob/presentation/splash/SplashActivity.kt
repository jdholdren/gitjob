package com.mindlesscreations.gitjob.presentation.splash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.mindlesscreations.gitjob.presentation.home.HomeActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
