package com.mindlesscreations.gitjob.data.repo

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.*
import com.mindlesscreations.gitjob.domain.gateways.LocationGateway
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LocationRepo(private val client: FusedLocationProviderClient, private val context: Context) : LocationGateway {
    override fun requestLocation(): Observable<Location> {
        return Flowable.create({ e: Emitter<Location> ->
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Make the request
                val request = LocationRequest()
                request.interval = 2000
                request.fastestInterval = 500
                request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

                client.requestLocationUpdates(request, object : LocationCallback() {
                    override fun onLocationResult(lr: LocationResult?) {
                        if (lr != null && lr.lastLocation != null) {
                            client.removeLocationUpdates(this)
                            e.onNext(lr.locations[lr.locations.size - 1])
                            e.onComplete()
                        }
                    }

                    override fun onLocationAvailability(la: LocationAvailability?) {
                        if (la?.isLocationAvailable == false) {
                            e.onError(Throwable("Device location unavailable"))
                            client.removeLocationUpdates(this)
                        }
                    }
                }, null)
            } else {
                // Should never be hit but account for it anyway
                e.onError(Throwable("Location permission not granted"))
            }
        }, BackpressureStrategy.BUFFER).toObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
    }
}