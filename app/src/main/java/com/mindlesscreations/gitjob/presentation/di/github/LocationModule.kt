package com.mindlesscreations.gitjob.presentation.di.github

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mindlesscreations.gitjob.data.repo.LocationRepo
import com.mindlesscreations.gitjob.domain.gateways.LocationGateway
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {

    @Provides
    @Singleton
    fun locationGateway(client: FusedLocationProviderClient, app: Application): LocationGateway {
        return LocationRepo(client, app)
    }

    @Provides
    @Singleton
    fun fusedLocationProvider(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }
}