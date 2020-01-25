package com.smsol.rateit.model

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val location: Location,
    val viewport: Viewport
)