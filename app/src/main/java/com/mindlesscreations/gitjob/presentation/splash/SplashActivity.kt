package com.mindlesscreations.gitjob.presentation.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.mindlesscreations.gitjob.presentation.jobList.JobListActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, JobListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
