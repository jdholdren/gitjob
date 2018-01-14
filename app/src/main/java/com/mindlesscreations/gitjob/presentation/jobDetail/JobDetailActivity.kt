package com.mindlesscreations.gitjob.presentation.jobDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.domain.entities.Job
import kotlinx.android.synthetic.main.activity_job_detail.*

class JobDetailActivity : AppCompatActivity() {

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

    private fun renderJob(job: Job) {
        this.company.text = job.company
        this.job_title.text = job.title
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
