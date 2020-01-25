package com.smsol.rateit.model

data class Responsemain(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<Result>,
    val status: String
)