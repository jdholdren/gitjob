package com.mindlesscreations.gitjob.presentation.jobDetail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.domain.entities.Job
import kotlinx.android.synthetic.main.activity_job_detail.*
import java.util.*


class JobDetailFragment : Fragment() {

    private var applyAnimator: AnimatorSet? = null
    private var applyHidden = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_job_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the arguments
        val job: Job? = this.arguments?.getParcelable(EXTRA_JOB)
        if (job != null) {
            this.renderJob(job)
        } else {
            findNavController().popBackStack()
        }

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val appCompatActivity = this.requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(toolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    //region Instance Methods

    /**
     * Sets the job properties on the needed fields in the correct views. Also sets up click listeners
     * for the 'apply now' button
     */
    private fun renderJob(job: Job) {
        this.company_and_location.text = String.format(
                Locale.US,
                getString(R.string.company_and_location),
                job.company,
                job.location
        )
        this.job_title.text = job.title

        Glide.with(this)
                .load(job.companyLogo)
                .apply(RequestOptions().centerCrop())
                .transition(withCrossFade())
                .into(this.image_background)

        // Set the description
        this.html_text_view.setHtml(job.description)

        this.setupApplyButton(job)
    }

    private fun setupApplyButton(job: Job) {
        if (job.companyUrl == null) {
            this.apply_button.visibility = View.GONE
            return
        }

        this.apply_button.setOnClickListener {
            this.launchUrl(job.companyUrl)
        }

        // The scrollY won't change until the toolbar is collapsed, so two listeners must be used
        var oldOffset = 0
        this.app_bar.addOnOffsetChangedListener { _, verticalOffset ->
            if (oldOffset < verticalOffset && !this.applyHidden) {
                this.hideApply()
            } else if (this.applyHidden && (verticalOffset == 0 || verticalOffset < oldOffset)) {
                this.showApply()
            }

            oldOffset = verticalOffset
        }

        this.scroll_view.setOnScrollChangeListener { _: NestedScrollView?, scrollX: Int, scrollY: Int, _: Int, oldScrollY: Int ->

            // Check if it's moving down or near the bottom
            if ((oldScrollY < scrollY && !this.applyHidden) || 25 > (this.scroll_view.bottom - (this.scroll_view.height + scrollY))) {
                this.hideApply()
            } else if (oldScrollY > scrollX && this.applyHidden) {
                this.showApply()
            }
        }
    }

    private fun hideApply() {
        this.applyHidden = true

        val objectAnimator = ObjectAnimator.ofFloat(
                this.apply_button,
                "translationY",
                this.apply_button.translationY,
                200f
        )
        objectAnimator.duration = 225
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()

        this.applyAnimator?.cancel()

        this.applyAnimator = AnimatorSet()
        this.applyAnimator?.play(objectAnimator)

        this.applyAnimator?.start()
    }

    private fun showApply() {
        this.applyHidden = false

        val objectAnimator = ObjectAnimator.ofFloat(
                this.apply_button,
                "translationY",
                this.apply_button.translationY,
                0f
        )
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.duration = 195

        this.applyAnimator?.cancel()

        this.applyAnimator = AnimatorSet()
        this.applyAnimator?.play(objectAnimator)

        this.applyAnimator?.start()
    }

    private fun launchUrl(url: String) {
        val intent = CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this.requireContext(), R.color.colorPrimary))
                .build()

        intent.launchUrl(this.requireContext(), Uri.parse(url))
    }

    //endregion

    companion object {
        const val EXTRA_JOB = "detail.extras.job"

        fun createBundle(job: Job): Bundle {
            return Bundle().apply {
                putParcelable(EXTRA_JOB, job)
            }
        }
    }
}
