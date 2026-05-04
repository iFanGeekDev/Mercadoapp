package com.mercadoapp.domain.model

data class Address(
    val id: String = "",
    val alias: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val isDefault: Boolean = false
)
