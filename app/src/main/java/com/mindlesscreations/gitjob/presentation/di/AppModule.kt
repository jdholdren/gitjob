package com.mindlesscreations.gitjob.presentation.di

import android.app.Application
import com.mindlesscreations.gitjob.domain.ExecutionWrapper
import com.mindlesscreations.gitjob.execution.ExecutionWrapperImpl
import com.mindlesscreations.gitjob.presentation.di.github.GithubModule
import com.mindlesscreations.gitjob.presentation.di.viewModel.ViewModelModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, GithubModule::class])
class AppModule(private val app: Application) {

    @Provides
    fun app(): Application {
        return this.app
    }

    @Provides
    @Singleton
    fun executionWrapper(): ExecutionWrapper {
        return ExecutionWrapperImpl()
    }
}