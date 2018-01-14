package com.mindlesscreations.gitjob.presentation.jobList

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.InputMethodManager
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.domain.entities.Job
import com.mindlesscreations.gitjob.domain.entities.Resource
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

        // Set the toolbar as the support action bar
        this.setSupportActionBar(this.toolbar)

        this.setupRecycler()
        this.setupViewModel()
        this.setupFields()
        this.setupSwipeRefresh()

        val (keywords, location) = getParams()
        this.viewModel.init(keywords, location)
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
     * Adds on enter listeners for each field
     */
    private fun setupFields() {
        this.keywords.setOnEditorActionListener { _, _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.keywords.windowToken, 0)
            refresh()
            true
        }
        this.location.setOnEditorActionListener { _, _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.location.windowToken, 0)
            refresh()
            true
        }
    }

    /**
     * Gets the reference to the view model and attaches observes the live data
     */
    private fun setupViewModel() {
        this.viewModel = ViewModelProviders.of(this, this.viewModelFactory)
                .get(JobListViewModel::class.java)
        this.viewModel.data.observe({ this.lifecycle }, { res ->
            if (res != null) this.setJobs(res)
        })
    }

    /**
     * Attaches the refresh listener to grab the current settings and refresh
     */
    private fun setupSwipeRefresh() {
        this.swipe_refresh.setOnRefreshListener {
            refresh()
        }
    }

    private fun setJobs(resource: Resource<List<Job>>) {
        when (resource.status) {
            Resource.Status.LOADING -> {
                this.swipe_refresh.isRefreshing = true
            }
            Resource.Status.ERROR -> {
                // Turn off the loader
                this.swipe_refresh.isRefreshing = false

                Snackbar.make(this.recycler_view, resource.message!!, Snackbar.LENGTH_INDEFINITE)
                        .show()
                // TODO Give the option to refresh
            }
            Resource.Status.SUCCESS -> {
                // Turn off the loader
                this.swipe_refresh.isRefreshing = false

                // Set the job list on the adapter
                this.adapter.setJobs(resource.data!!)
            }
        }
    }

    /**
     * Grabs the view's currently entered parameters
     */
    private fun getParams(): Params {
        return Params(
                this.keywords.text.toString(),
                this.location.text.toString()
        )
    }

    /**
     * Triggers a reload of the screen, getting the current inputs and passing them along
     */
    private fun refresh() {
        val (keywords, location) = getParams()
        this.viewModel.loadJobs(keywords, location)
    }

    //endregion

    //region JobAdapter.OnClickListener implementation

    override fun onJobClicked(job: Job) {
        this.startActivity(JobDetailActivity.createIntent(this, job))
    }

    //endregion

    //region DI

    override fun doInjection(component: AppComponent) {
        component.inject(this)
    }

    //endregion

    data class Params(val keywords: String?, val location: String?)
}
