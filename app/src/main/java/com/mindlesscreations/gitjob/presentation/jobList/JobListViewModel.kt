package com.mindlesscreations.gitjob.presentation.jobList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindlesscreations.gitjob.domain.ExecutionWrapper
import com.mindlesscreations.gitjob.domain.entities.Job
import com.mindlesscreations.gitjob.domain.entities.Resource
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
    private val _data = MutableLiveData<Resource<List<Job>>>()
    val data: LiveData<Resource<List<Job>>>
        get() = _data

    fun init(keywords: String?, location: String?, useGps: Boolean) {
        if (!hasBeenInit) {
            this.loadJobs(keywords, location, useGps)

            this.hasBeenInit = true
        }
    }

    fun loadJobs(keywords: String?, location: String?, useGps: Boolean) {
        this.disposable?.dispose()

        this._data.value = Resource.loading(emptyList())

        this.disposable = this.wrapper.wrap(this.jobGateway.getJobs(keywords, location, useGps))
                .subscribe({ jobs ->
                    this._data.value = Resource.success(jobs)
                }, { e ->
                    this._data.value = Resource.error(e.message!!, null)
                })
    }

    override fun onCleared() {
        this.disposable?.dispose()
    }
}