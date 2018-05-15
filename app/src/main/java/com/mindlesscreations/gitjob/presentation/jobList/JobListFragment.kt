package com.mindlesscreations.gitjob.presentation.jobList

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.domain.entities.Job
import com.mindlesscreations.gitjob.domain.entities.Resource
import com.mindlesscreations.gitjob.presentation.base.InjectedFragment
import com.mindlesscreations.gitjob.presentation.decorator.VerticalSpaceDecorator
import com.mindlesscreations.gitjob.presentation.di.AppComponent
import com.mindlesscreations.gitjob.presentation.di.viewModel.ViewModelFactory
import com.mindlesscreations.gitjob.presentation.jobDetail.JobDetailFragment
import com.mindlesscreations.gitjob.presentation.jobList.adapter.JobAdapter
import javax.inject.Inject

class JobListFragment : InjectedFragment(), JobAdapter.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: JobListViewModel

    private val adapter = JobAdapter()

    private var locationAnimator: AnimatorSet? = null

    private lateinit var location_switch: Switch
    private lateinit var gps_container: View
    private lateinit var recycler_view: RecyclerView
    private lateinit var keywords: EditText
    private lateinit var location: EditText
    private lateinit var swipe_refresh: SwipeRefreshLayout
    private lateinit var empty_state: View
    private lateinit var location_container: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)

        this.location_switch = view.findViewById(R.id.location_switch)
        this.gps_container = view.findViewById(R.id.gps_container)
        this.recycler_view = view.findViewById(R.id.recycler_view)
        this.keywords = view.findViewById(R.id.keywords)
        this.location = view.findViewById(R.id.location)
        this.swipe_refresh = view.findViewById(R.id.swipe_refresh)
        this.empty_state = view.findViewById(R.id.empty_state)
        this.location_container = view.findViewById(R.id.location_container)

        this.setupRecycler()
        this.setupViewModel()
        this.setupFields()
        this.setupSwipeRefresh()

        val (keywords, location, useGps) = getParams()
        this.viewModel.init(keywords, location, useGps)

        return view
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

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
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
        this.recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
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
            val imm = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.keywords.windowToken, 0)
            refresh()
            true
        }
        this.location.setOnEditorActionListener { _, _, _ ->
            val imm = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.location.windowToken, 0)
            refresh()
            true
        }

        if (ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && !ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Hide the switch
            this.gps_container.visibility = View.INVISIBLE
            return
        }

        this.location_switch.setOnCheckedChangeListener { _, isChecked ->
            // Can get into a weird state where you can input the text if you hide it with keyboard up
            val imm = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.location.windowToken, 0)

            var wasRequested = false

            // Check for the location permission
            if (isChecked && ContextCompat
                            .checkSelfPermission(
                                    this.activity!!,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED) {
                // Returning after this function does not execute, so set wasRequest to true so that
                // the rest of this function doesn't execute
                wasRequested = true

                ActivityCompat.requestPermissions(
                        this.activity!!,
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

                Snackbar.make(this.recycler_view, resource.message!!, Snackbar.LENGTH_INDEFINITE)
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
        findNavController().navigate(R.id.detail, JobDetailFragment.createBundle(job))
    }

    //endregion

    //region DI

    override fun doInjection(component: AppComponent) {
        component.inject(this)
    }

    //endregion

    data class Params(val keywords: String?, val location: String?, val useGps: Boolean)

    companion object {
        const val RC_LOCATION = 0
    }
}
