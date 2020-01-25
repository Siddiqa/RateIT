package com.smsol.rateit.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smsol.rateit.model.Rating


class LocalPrefrence {

    private var editor: SharedPreferences.Editor
    private var pref: SharedPreferences

    companion object {
        private val prefname = "RateItPref"
        private val myrating = "MyRating"

    }


    constructor(context: Context) {
        pref = context.getSharedPreferences(prefname, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    fun getRatings(): ArrayList<Rating>? {
        val gson = Gson()
        val json = pref.getString(myrating, null)
        if (json != null) {
            val type = object : TypeToken<ArrayList<Rating>>() {
            }.type
            return gson.fromJson(json, type)
        } else {
            return null
        }


    }

    fun addRatings(rating: Rating) {
        var status = false
        var list = getRatings()
        if (list != null && list.size > 0) {

            for (i in 0 until list.size) {
                if (list[i].placeid == rating.placeid && list[i].deviceid == rating.deviceid) {
                    list[i].rating = rating.rating
                    status = true
                    break
                }
            }
            if (!status) {
                list.add(rating)
            }

        } else {
            list = ArrayList<Rating>()
            list.add(rating)
        }
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(myrating, json)
        editor.apply()
    }
}