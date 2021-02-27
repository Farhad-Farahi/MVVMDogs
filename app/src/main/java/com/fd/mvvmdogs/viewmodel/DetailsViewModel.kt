package com.fd.mvvmdogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fd.mvvmdogs.db.DogDatabase
import com.fd.mvvmdogs.model.DogBreedModel
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application) : BaseViewModel(application) {

    val dogLiveData = MutableLiveData<DogBreedModel>()

    fun fetch(uuid: Int) {
        launch {
            val dog = DogDatabase(getApplication()).dogDao().getDog(uuid)
            dogLiveData.value = dog
        }

    }

}