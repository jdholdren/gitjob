package com.mindlesscreations.gitjob.data.repo

import com.mindlesscreations.gitjob.data.retrofit.JobApi
import com.mindlesscreations.gitjob.domain.entities.Job
import com.mindlesscreations.gitjob.domain.gateways.JobGateway
import io.reactivex.Observable

/**
 * Handles the retrieval and sync strategy for jobs
 */
class JobRepo(
        private val api: JobApi
) : JobGateway {

    override fun getJobs(query: String?, location: String?): Observable<List<Job>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}