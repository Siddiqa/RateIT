package com.smsol.rateit.respository

import com.smsol.rateit.model.Responsemain
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RetrofitApi {
    @GET("json")
    fun getnearby(@QueryMap map:HashMap<String,String>):Single<Responsemain>
}