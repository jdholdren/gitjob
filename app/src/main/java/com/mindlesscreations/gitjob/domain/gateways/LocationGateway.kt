package com.mindlesscreations.gitjob.domain.gateways

import android.location.Location
import io.reactivex.Observable

interface LocationGateway {
    fun requestLocation(): Observable<Location>
}