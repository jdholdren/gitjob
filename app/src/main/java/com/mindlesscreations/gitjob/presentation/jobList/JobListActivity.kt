package com.mindlesscreations.gitjob.presentation.jobList

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.presentation.base.InjectedActivity
import com.mindlesscreations.gitjob.presentation.di.AppComponent
import com.mindlesscreations.gitjob.presentation.di.viewModel.ViewModelFactory
import javax.inject.Inject

class JobListActivity : InjectedActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: JobListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        this.setupViewModel()

        this.viewModel.init()
    }

    //region Instance Methods

    private fun setupViewModel() {
        this.viewModel = ViewModelProviders.of(this, this.viewModelFactory)
                .get(JobListViewModel::class.java)

        // TODO Observe the jobs data
    }

    //endregion

    override fun doInjection(component: AppComponent) {
        component.inject(this)
    }
}
