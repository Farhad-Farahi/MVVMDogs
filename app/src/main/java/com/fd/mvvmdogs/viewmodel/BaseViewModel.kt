package com.fd.mvvmdogs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * difference between ViewModel And application View model is the presence of the application context
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) ,CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main      //it means job is running after that we will back to main thread

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}