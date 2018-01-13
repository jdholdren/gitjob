package com.mindlesscreations.gitjob.domain

import io.reactivex.Observable

interface ExecutionWrapper {
    fun <T> obs(obs: Observable<T>): Observable<T>
}