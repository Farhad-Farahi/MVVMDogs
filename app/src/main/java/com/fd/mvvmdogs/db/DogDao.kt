package com.fd.mvvmdogs.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fd.mvvmdogs.model.DogBreedModel

@Dao
interface DogDao {



    @Insert
    suspend fun insertAll(vararg dog : DogBreedModel):List<Long>

    @Query("SELECT * FROM dogbreedmodel")
    suspend fun getAllDogs():List<DogBreedModel>

    @Query("SELECT * FROM dogbreedmodel WHERE uuid = :dogId")
    suspend fun getDog(dogId : Int):DogBreedModel

    @Query("DELETE FROM dogbreedmodel")
    suspend fun deleteAllDogs()


}