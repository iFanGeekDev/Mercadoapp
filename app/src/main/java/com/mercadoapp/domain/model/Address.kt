package com.mercadoapp.domain.model

data class Address(
    val id: String = "",
    val alias: String,
    val street: String,
    val distrito: String,
    val provincia: String,
    val departamento: String,
    val isDefault: Boolean = false
)
