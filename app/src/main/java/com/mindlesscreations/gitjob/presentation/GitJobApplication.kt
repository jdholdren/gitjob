package com.mindlesscreations.gitjob.presentation

import android.app.Application
import com.mindlesscreations.gitjob.presentation.di.AppComponent
import com.mindlesscreations.gitjob.presentation.di.AppModule
import com.mindlesscreations.gitjob.presentation.di.DaggerAppComponent

class GitJobApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        this.initComponent()

        this.appComponent.inject(this)
    }

    private fun initComponent() {
        this.appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}