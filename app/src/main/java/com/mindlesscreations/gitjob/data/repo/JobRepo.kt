package com.mindlesscreations.gitjob.data.repo

import com.mindlesscreations.gitjob.data.retrofit.JobApi
import com.mindlesscreations.gitjob.domain.entities.Job
import com.mindlesscreations.gitjob.domain.gateways.JobGateway
import com.mindlesscreations.gitjob.domain.gateways.LocationGateway
import io.reactivex.Observable

/**
 * Handles the retrieval and sync strategy for jobs
 */
class JobRepo(
        private val api: JobApi,
        private val locationGateway: LocationGateway
) : JobGateway {

    override fun getJobs(query: String?, location: String?, useCourseLocation: Boolean): Observable<List<Job>> {
        if (!useCourseLocation) {
            return this.api.fetchJobs(query, location)
        } else {
            // Await for a gps location
            return this.locationGateway.requestLocation()
                    .flatMap { gpsLoc ->
                        this.api.fetchJobsWithLatLong(query, gpsLoc.latitude, gpsLoc.longitude)
                    }
        }
    }
}