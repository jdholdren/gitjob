package com.mindlesscreations.gitjob.presentation.jobList

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.presentation.GitJobApplication
import com.mindlesscreations.gitjob.presentation.di.DaggerAppComponent
import com.mindlesscreations.gitjob.presentation.di.viewModel.ViewModelFactory
import javax.inject.Inject

class JobListActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        (this.application as GitJobApplication).appComponent.inject(this)
    }
}
