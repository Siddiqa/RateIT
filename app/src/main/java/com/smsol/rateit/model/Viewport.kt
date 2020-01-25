package com.smsol.rateit.model

import kotlinx.serialization.Serializable

@Serializable
data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)