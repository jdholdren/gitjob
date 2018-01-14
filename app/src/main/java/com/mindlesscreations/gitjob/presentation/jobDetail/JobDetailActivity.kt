package com.mindlesscreations.gitjob.presentation.jobDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mindlesscreations.gitjob.R
import kotlinx.android.synthetic.main.activity_job_detail.*

class JobDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setupView()
    }

    //region Instance Methods

    private fun setupView() {
        this.setContentView(R.layout.activity_job_detail)
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //endregion

    companion object {

        private val EXTRA_JOB_ID = "jobDetail.extras.jobId"

        fun createIntent(context: Context, jobId: String): Intent {
            val intent = Intent(context, JobDetailActivity::class.java)
            intent.putExtra(EXTRA_JOB_ID, jobId)

            return intent
        }
    }
}
