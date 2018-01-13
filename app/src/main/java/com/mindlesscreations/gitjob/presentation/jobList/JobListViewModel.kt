package com.mindlesscreations.gitjob.presentation.jobList

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.mindlesscreations.gitjob.domain.ExecutionWrapper
import com.mindlesscreations.gitjob.domain.entities.Job
import com.mindlesscreations.gitjob.domain.gateways.JobGateway
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Responsible for the coordinating between the gateway and the view state
 */
class JobListViewModel @Inject constructor(
        private val wrapper: ExecutionWrapper,
        private val jobGateway: JobGateway
) : ViewModel() {

    private var hasBeenInit = false

    private var disposable: Disposable? = null

    // The observable data for the view to display
    private val _data = MutableLiveData<List<Job>>()
    private val data: LiveData<List<Job>>
        get() = _data

    fun init() {
        if (!hasBeenInit) {
            this.loadJobs()
        }
    }

    fun loadJobs() {
        this.disposable?.dispose()

        this.disposable = this.wrapper.wrap(this.jobGateway.getJobs(null, null))
                .subscribe({ jobs ->
                    val gson = Gson()
                    Log.v("Jobs", gson.toJson(jobs))
                }, { e ->
                    e.printStackTrace()
                })
    }

    override fun onCleared() {
        this.disposable?.dispose()
    }
}