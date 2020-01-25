package com.app.forecast.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.smsol.rateit.respository.RetrofitApi
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit



object GlobalRetrofit {

    private var retrofit: Retrofit? = null

    fun getClient(): RetrofitApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                .build()
        }
        return retrofit!!.create(RetrofitApi::class.java)
    }

    fun isNetworkConnected(context :Context): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var info =cm.activeNetworkInfo as NetworkInfo
        return  info.isConnected

    }
    fun verifyAvailableNetwork(activity: AppCompatActivity):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }

}