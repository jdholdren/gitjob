package com.mindlesscreations.gitjob.presentation.jobList

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
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

    private var locationAnimator: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        // Set the toolbar as the support action bar
        this.setSupportActionBar(this.toolbar)

        this.setupRecycler()
        this.setupViewModel()
        this.setupFields()
        this.setupSwipeRefresh()

        val (keywords, location, useGps) = getParams()
        this.viewModel.init(keywords, location, useGps)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RC_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refresh()
                } else {
                    // Permission denied, unswitch
                    this.location_switch.isChecked = false

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        this.gps_container.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    //region Instance Methods

    /**
     * Attaches the adapter to the recycler, click listeners, and decorator
     */
    private fun setupRecycler() {
        this.recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Hide the switch
            this.gps_container.visibility = View.INVISIBLE
            return
        }

        this.location_switch.setOnCheckedChangeListener { _, isChecked ->
            // Can get into a weird state where you can input the text if you hide it with keyboard up
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.location.windowToken, 0)

            var wasRequested = false

            // Check for the location permission
            if (isChecked && ContextCompat
                    .checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                // Returning after this function does not execute, so set wasRequest to true so that
                // the rest of this function doesn't execute
                wasRequested = true

                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        RC_LOCATION
                )
            }

            if (isChecked) {
                this.locationTextEnabled(false)
            } else {
                this.locationTextEnabled(true)
            }

            if (!wasRequested) {
                refresh()
            }
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

                com.google.android.material.snackbar.Snackbar.make(this.recycler_view, resource.message!!, com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, { refresh() })
                        .show()
            }
            Resource.Status.SUCCESS -> {
                // Turn off the loader
                this.swipe_refresh.isRefreshing = false

                // Set the job list on the adapter
                this.adapter.setJobs(resource.data!!)

                this.empty_state.visibility = if (resource.data.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    /**
     * Grabs the view's currently entered parameters
     */
    private fun getParams(): Params {
        return Params(
                this.keywords.text.toString(),
                this.location.text.toString(),
                this.location_switch.isChecked
        )
    }

    /**
     * Triggers a reload of the screen, getting the current inputs and passing them along
     */
    private fun refresh() {
        val (keywords, location, useGps) = getParams()
        this.viewModel.loadJobs(keywords, location, useGps)
    }

    /**
     * Based on true/false, hides or shows the location text field
     */
    private fun locationTextEnabled(enabled: Boolean) {
        this.locationAnimator?.cancel()

        // Get the current height
        val height = this.location_container.measuredHeight

        // Measure for max height
        this.location_container.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val maxHeight = this.location_container.measuredHeight

        // Prevent expansion at max size from making the field disappear
        if (enabled && maxHeight == height) return

        val start = if (enabled) 0f else 1f
        val end = if (enabled) 1f else 0f
        val valAnimator = ValueAnimator.ofFloat(start, end)
        valAnimator.duration = if (enabled) 225 else 195
        valAnimator.interpolator = AccelerateDecelerateInterpolator()

        valAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            this.location_container.layoutParams.height = if (enabled) {
                (animatedValue * (maxHeight - height)).toInt()
            } else {
                (animatedValue * height).toInt()
            }
            this.location_container.requestLayout()
        }

        this.locationAnimator = AnimatorSet()
        this.locationAnimator?.play(valAnimator)

        this.locationAnimator?.start()
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

    data class Params(val keywords: String?, val location: String?, val useGps: Boolean)

    companion object {
        val RC_LOCATION = 0
    }
}
