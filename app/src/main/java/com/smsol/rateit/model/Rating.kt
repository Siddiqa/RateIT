package com.smsol.rateit.model

data class Rating(
    val placeid: String,
    val name: String,
    val address: String,
    var rating: Int,
    val deviceid: String
)