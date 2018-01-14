package com.mindlesscreations.gitjob.presentation.jobDetail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.domain.entities.Job
import kotlinx.android.synthetic.main.activity_job_detail.*
import java.util.*

class JobDetailActivity : AppCompatActivity() {

    private var applyAnimator: AnimatorSet? = null
    private var applyHidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setupView()

        // Get the job from the intent
        val job = this.intent?.getParcelableExtra<Job>(EXTRA_JOB)

        if (job == null) {
            this.finish()
        } else {
            this.renderJob(job)
        }
    }

    //region Instance Methods

    private fun setupView() {
        this.setContentView(R.layout.activity_job_detail)
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

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
        // TODO Attach on click

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

    //endregion

    companion object {

        private val EXTRA_JOB = "jobDetail.extras.job"

        fun createIntent(context: Context, job: Job): Intent {
            val intent = Intent(context, JobDetailActivity::class.java)
            intent.putExtra(EXTRA_JOB, job)

            return intent
        }
    }
}
