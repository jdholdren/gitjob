package com.mindlesscreations.gitjob.presentation.di

import android.app.Application
import com.mindlesscreations.gitjob.presentation.GitJobApplication
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {

    fun app(): Application

    //region Injection

    fun inject(app: GitJobApplication)

    //endregion
}