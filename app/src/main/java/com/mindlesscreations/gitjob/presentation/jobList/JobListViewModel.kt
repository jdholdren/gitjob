package com.mindlesscreations.gitjob.presentation.jobList

import android.arch.lifecycle.ViewModel
import com.mindlesscreations.gitjob.domain.gateways.JobGateway
import javax.inject.Inject

class JobListViewModel @Inject constructor(
        private val jobGateway: JobGateway
): ViewModel() {

    override fun onCleared() {
        super.onCleared()
    }
}