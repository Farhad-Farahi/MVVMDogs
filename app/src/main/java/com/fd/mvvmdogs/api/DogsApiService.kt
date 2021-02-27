package com.fd.mvvmdogs.api

import com.fd.mvvmdogs.model.DogBreedModel
import com.fd.mvvmdogs.utils.Constants.BASE_URL
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class DogsApiService {

    // GsonConverterFactory do it automatically in separate thread
    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(DogsApi::class.java)


    //Single is basically an observable that simply omits data once and then finishes.
    fun getDogs():Single<List<DogBreedModel>>{
        return api.getDogs()
    }
}