package com.mindlesscreations.gitjob.presentation.jobList

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.domain.entities.Job
import com.mindlesscreations.gitjob.domain.entities.Resource
import com.mindlesscreations.gitjob.domain.entities.Status
import com.mindlesscreations.gitjob.presentation.base.InjectedActivity
import com.mindlesscreations.gitjob.presentation.decorator.VerticalSpaceDecorator
import com.mindlesscreations.gitjob.presentation.di.AppComponent
import com.mindlesscreations.gitjob.presentation.di.viewModel.ViewModelFactory
import com.mindlesscreations.gitjob.presentation.jobDetail.JobDetailActivity
import com.mindlesscreations.gitjob.presentation.jobList.adapter.JobAdapter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class JobListActivity : InjectedActivity(), JobAdapter.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: JobListViewModel

    private val adapter = JobAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        this.setupRecycler()
        this.setupViewModel()

        this.viewModel.init()
    }

    //region Instance Methods

    /**
     * Attaches the adapter to the recycler, click listeners, and decorator
     */
    private fun setupRecycler() {
        this.recycler_view.layoutManager = LinearLayoutManager(this)
        this.recycler_view.adapter = this.adapter
        this.recycler_view.addItemDecoration(VerticalSpaceDecorator(
                this.resources.getDimension(R.dimen.card_vertical_space).toInt()
        ))
        this.adapter.listener = this
    }

    /**
     * Gets the reference to the view model and attaches observes the live data
     */
    private fun setupViewModel() {
        this.viewModel = ViewModelProviders.of(this, this.viewModelFactory)
                .get(JobListViewModel::class.java)
        this.viewModel.data.observe({this.lifecycle}, { res ->
            if (res != null) this.setJobs(res)
        })
    }

    private fun setJobs(resource: Resource<List<Job>>) {
        when (resource.status) {
            Status.LOADING -> {
            } // TODO Show the swipe loader
            Status.ERROR -> {
                Snackbar.make(this.recycler_view, resource.message!!, Snackbar.LENGTH_INDEFINITE)
                        .show()
                // Give the option to refresh
            }
            Status.SUCCESS -> {
                // Set the job list on the adapter
                this.adapter.setJobs(resource.data!!)
            }
        }
    }

    //endregion

    //region JobAdapter.OnClickListener implementation

    override fun onJobClicked(job: Job) {
        this.startActivity(JobDetailActivity.createIntent(this, job.id))
    }

    //endregion

    //region DI

    override fun doInjection(component: AppComponent) {
        component.inject(this)
    }

    //endregion
}
