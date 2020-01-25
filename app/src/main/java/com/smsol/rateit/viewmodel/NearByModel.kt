package com.smsol.rateit.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.forecast.utils.GlobalRetrofit
import com.smsol.rateit.model.Responsemain
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NearByModel:ViewModel() {
    private var TAG="NearByModel"
    fun getList(map: HashMap<String,String>):MutableLiveData<Responsemain>{
        var data=MutableLiveData<Responsemain>()
        var call=GlobalRetrofit.getClient().getnearby(map)
        call.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Responsemain>{
                override fun onSuccess(t: Responsemain) {
                   Log.e(TAG,"onSuccess ${t}")
                    data.postValue(t)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.e(TAG,"onError ${e.message}")
                }

            })

        return data
    }

}