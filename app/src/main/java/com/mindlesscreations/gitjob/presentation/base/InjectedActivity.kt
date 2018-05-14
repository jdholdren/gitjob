package com.mindlesscreations.gitjob.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mindlesscreations.gitjob.presentation.GitJobApplication
import com.mindlesscreations.gitjob.presentation.di.AppComponent

/**
 * Class that provides the injection method for children activities
 */
abstract class InjectedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.doInjection((this.application as GitJobApplication).appComponent)
    }

    /**
     * Makes the activity do injection during onCreate
     */
    abstract fun doInjection(component: AppComponent)
}