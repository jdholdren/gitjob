package com.mindlesscreations.gitjob.data

import com.mindlesscreations.gitjob.domain.entities.Job
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface JobApi {

    @GET("/")
    fun fetchJobs(
            @Query("description") query: String?,
            @Query("location") location: String?
    ): Observable<List<Job>>
}