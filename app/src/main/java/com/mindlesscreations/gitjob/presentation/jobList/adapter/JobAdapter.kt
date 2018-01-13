package com.mindlesscreations.gitjob.presentation.jobList.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindlesscreations.gitjob.R
import com.mindlesscreations.gitjob.domain.entities.Job
import kotlinx.android.synthetic.main.item_job.view.*

class JobAdapter : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    private var jobs: List<Job> = emptyList()

    /**
     * Sets the info on the card and loads the image
     */
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = this.jobs[position]

        // Set the field data
        holder.company.text = job.company
        holder.title.text = job.title
        holder.location.text = job.location

                // Load the image
                Glide.with(holder.itemView?.context!!)
                .load(job.companyLogo)
                .apply(RequestOptions().placeholder(R.drawable.ic_image_black_24dp))
                .apply(RequestOptions.fitCenterTransform())
                .into(holder.image)
    }

    /**
     * Returns the size of the backing field
     */
    override fun getItemCount(): Int {
        return this.jobs.size
    }

    /**
     * Inflates the view and makes the {@link JobViewHolder}
     */
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): JobViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(viewType, parent, false)
        return JobViewHolder(v)
    }

    /**
     * Returns the layout id, would be several different ones if there were more than 1 item type
     */
    override fun getItemViewType(position: Int): Int {
        return R.layout.item_job
    }

    fun setJobs(current: List<Job>) {
        val result = DiffUtil.calculateDiff(JobDiffCallback(this.jobs, current))
        this.jobs = current

        result.dispatchUpdatesTo(this)
    }

    /**
     * View holder for a job inside the adapter
     */
    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.job_image
        val company: TextView = itemView.company
        val title: TextView = itemView.title
        val location: TextView = itemView.location
    }

    /**
     * Callback for getting the diff results when setting the next list of jobs
     */
    class JobDiffCallback(
            private val old: List<Job>,
            private val current: List<Job>
    ) : DiffUtil.Callback() {

        /**
         * Compares two jobs' Id's
         */
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldJob = this.old[oldItemPosition]
            val newJob = this.current[newItemPosition]

            return oldJob.id == newJob.id
        }

        /**
         * Returns the old size
         */
        override fun getOldListSize(): Int {
            return this.old.size
        }

        /**
         * Returns the current list's size
         */
        override fun getNewListSize(): Int {
            return this.current.size
        }

        /**
         * Since the contents will likely not change based if they share an id, always return true
         */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }
    }
}