package com.mindlesscreations.gitjob.presentation.di.viewModel

import androidx.lifecycle.ViewModel
import com.mindlesscreations.gitjob.presentation.jobList.JobListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import androidx.lifecycle.ViewModelProvider

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(JobListViewModel::class)
    abstract fun bindJobList(viewModel: JobListViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}