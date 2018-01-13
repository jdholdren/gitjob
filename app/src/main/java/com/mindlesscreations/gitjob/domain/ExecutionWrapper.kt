package com.mindlesscreations.gitjob.domain

import io.reactivex.Observable

interface ExecutionWrapper {
    fun <T> wrap(obs: Observable<T>): Observable<T>
}