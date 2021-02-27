package com.fd.mvvmdogs.api

import com.fd.mvvmdogs.model.DogBreedModel
import com.fd.mvvmdogs.utils.Constants.ALL_DOGS
import io.reactivex.Single
import retrofit2.http.GET

interface DogsApi {

    @GET(ALL_DOGS)
    fun getDogs(): Single<List<DogBreedModel>>


}