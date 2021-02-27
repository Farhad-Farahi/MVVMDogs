package com.fd.mvvmdogs.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fd.mvvmdogs.model.DogBreedModel

@Dao
interface DogDao {


    //var arg means that its basically shorthand for variable arguments or multiple arguments
    //so we can pass this function as many object of DogBreedModel as we like
    //list<Long> = list from ids so the size of this list is the size of the dogs  we provide here
    @Insert
    suspend fun insertAll(vararg dog : DogBreedModel):List<Long>

    @Query("SELECT * FROM dogbreedmodel")
    suspend fun getAllDogs():List<DogBreedModel>

    @Query("SELECT * FROM dogbreedmodel WHERE uuid = :dogId")
    suspend fun getDog(dogId : Int):DogBreedModel

    @Query("DELETE FROM dogbreedmodel")
    suspend fun deleteAllDogs()


}