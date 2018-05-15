package com.mindlesscreations.gitjob.presentation.base

import android.os.Bundle
import android.support.v4.app.Fragment
import com.mindlesscreations.gitjob.presentation.GitJobApplication
import com.mindlesscreations.gitjob.presentation.di.AppComponent

abstract class InjectedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.doInjection((this.context?.applicationContext as GitJobApplication).appComponent)
    }

    /**
     * Makes the activity do injection during onCreate
     */
    abstract fun doInjection(component: AppComponent)
}