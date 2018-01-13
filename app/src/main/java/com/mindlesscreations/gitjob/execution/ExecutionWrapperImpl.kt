package com.mindlesscreations.gitjob.execution

import com.mindlesscreations.gitjob.domain.ExecutionWrapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ExecutionWrapperImpl : ExecutionWrapper {
    override fun <T> obs(obs: Observable<T>): Observable<T> {
        return obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}