package com.fd.mvvmdogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fd.mvvmdogs.api.DogsApiService
import com.fd.mvvmdogs.db.DogDatabase
import com.fd.mvvmdogs.model.DogBreedModel
import com.fd.mvvmdogs.utils.NotificationsHelper
import com.fd.mvvmdogs.utils.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException


class ListViewModel(application: Application) : BaseViewModel(application) {

    private var refreshTime = 5*60*1000*1000*1000L //5 min in nano second

    private var prefHelper = SharedPreferencesHelper(application)

    private val dogsService = DogsApiService()

    //the disposable will allow us to retrieve or to observe the observable that the api gives us
    // and it will avoid any memory leaks that might come with waiting for an observable while our application has been destroyed
    private val disposable = CompositeDisposable()


    val dogs = MutableLiveData<List<DogBreedModel>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()


    fun refresh() {
        checkCacheDuration()
        val updateTime =prefHelper.getUpdateTime()
        if (updateTime !=null && updateTime !=0L  && System.nanoTime() - updateTime <refreshTime ){
            fetchFromDatabase()
        }else{
            fetchFromRemote()
        }

    }
    private fun checkCacheDuration(){
        val cachePreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times(1000*1000*1000L) // convert second to nano second
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }
    }

    fun refreshBypassCache(){
        fetchFromRemote()
    }

    private fun fetchFromDatabase(){
        loading.value=true
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication() , "Dogs retrieved from database" ,Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * data from api
     */
    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread()) // call in background thread
                .observeOn(AndroidSchedulers.mainThread()) // result will be on mainThread
                .subscribeWith(object : DisposableSingleObserver<List<DogBreedModel>>() {
                    override fun onSuccess(dogList: List<DogBreedModel>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication() , "Dogs retrieved from Remote" ,Toast.LENGTH_SHORT).show()
                        NotificationsHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value =true
                        loading.value=false
                        e.printStackTrace()
                    }

                })
        )


    }
    private fun dogsRetrieved(dogList : List<DogBreedModel>){
        dogs.value =  dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list: List<DogBreedModel>){
        //Use Coroutine here
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*list.toTypedArray()) // for uuid
            var i = 0
            while (i<list.size){
                list[i].uuid = result[i].toInt()
                ++i
            }
            dogsRetrieved(list)

        }
        //store the time when we save information
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}

