package com.mindlesscreations.gitjob.presentation.di.github

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.data.repo.JobRepo
import com.mindlesscreations.gitjob.data.retrofit.JobApi
import com.mindlesscreations.gitjob.domain.gateways.JobGateway
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class GithubModule {
    @Provides
    @Singleton
    fun jobApi(app: Application): JobApi {

        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(app.getString(R.string.github_jobs_api_root))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(JobApi::class.java)
    }

    @Provides
    @Singleton
    fun jobGateway(api: JobApi): JobGateway {
        return JobRepo(api)
    }
}