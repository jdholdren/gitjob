package com.mindlesscreations.gitjob.domain.entities

/**
 * Represents a position, detailing the name, where it is, and the description
 */
class Job(
        val id: String,
        val title: String,
        val location: String,
        val company: String,
        val description: String
)