package com.mindlesscreations.gitjob.domain.entities

import android.os.Parcel
import android.os.Parcelable

/**
 * Represents a position, detailing the name, where it is, and the description.
 */
data class Job(
        val id: String,
        val title: String,
        val location: String?,
        val company: String,
        val description: String,
        val companyLogo: String?,
        val companyUrl: String?
) : Parcelable {

    /**
     * When making this object from a parcel
     */
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

    /**
     * Writes each of the properties out in order of the constructor
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.id)
        dest.writeString(this.title)
        dest.writeString(this.location)
        dest.writeString(this.company)
        dest.writeString(this.description)
        dest.writeString(this.companyLogo)
        dest.writeString(this.companyUrl)
    }

    /**
     * Typical return for a parcelable
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Required by the parcelable interface
     */
    companion object CREATOR : Parcelable.Creator<Job> {
        override fun createFromParcel(parcel: Parcel): Job {
            return Job(parcel)
        }

        override fun newArray(size: Int): Array<Job?> {
            return arrayOfNulls(size)
        }
    }
}