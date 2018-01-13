package com.mindlesscreations.gitjob.presentation.di

import android.app.Application
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.data.JobApi
import com.mindlesscreations.gitjob.domain.ExecutionWrapper
import com.mindlesscreations.gitjob.execution.ExecutionWrapperImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
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

    @Provides
    @Singleton
    fun jobApi(app: Application): JobApi {
        val retrofit = Retrofit.Builder()
                .baseUrl(app.getString(R.string.github_jobs_api_root))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(JobApi::class.java)
    }
}