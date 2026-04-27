package com.mercadoapp.domain.model

data class ProductSelection(
    val condition: Condition? = null,
    val processor: String? = null,
    val ramGb: Int? = null,
    val storageGb: Int? = null,
    val color: String? = null
)

data class SelectableOption<T>(
    val value: T,
    val enabled: Boolean
)

data class ProductOptionsState(
    val conditions: List<SelectableOption<Condition>>,
    val processors: List<SelectableOption<String>>,
    val rams: List<SelectableOption<Int>>,
    val storages: List<SelectableOption<Int>>,
    val colors: List<SelectableOption<String>>,
    val selectedVariant: ProductVariant?
)
