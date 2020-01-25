package com.smsol.rateit.model

import kotlinx.serialization.Serializable

@Serializable
data class OpeningHours(
    val open_now: Boolean
)