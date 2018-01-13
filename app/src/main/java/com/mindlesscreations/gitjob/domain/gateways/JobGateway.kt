package com.mindlesscreations.gitjob.domain.gateways

import com.mindlesscreations.gitjob.domain.entities.Job
import io.reactivex.Observable

/**
 * Represents the surface for retrieving jobs
 */
interface JobGateway {
    fun getJobs(query: String?, location: String?): Observable<List<Job>>
}