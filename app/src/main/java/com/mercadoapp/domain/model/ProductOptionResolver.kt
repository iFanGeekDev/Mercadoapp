package com.mercadoapp.domain.model

object ProductOptionResolver {

    fun resolve(product: Product, selection: ProductSelection): ProductOptionsState {
        val variants = product.variants

        fun ProductVariant.matchesIgnoring(option: String): Boolean {
            return (option == "condition" || selection.condition == null || condition == selection.condition) &&
                (option == "processor" || selection.processor == null || processor == selection.processor) &&
                (option == "ram" || selection.ramGb == null || ramGb == selection.ramGb) &&
                (option == "storage" || selection.storageGb == null || storageGb == selection.storageGb) &&
                (option == "color" || selection.color == null || color == selection.color)
        }

        val conditions = Condition.entries.map { option ->
            SelectableOption(option, variants.any { it.matchesIgnoring("condition") && it.condition == option && it.stock > 0 })
        }
        val processors = product.technicalSpecs.processor.map { option ->
            SelectableOption(option, variants.any { it.matchesIgnoring("processor") && it.processor == option && it.stock > 0 })
        }
        val rams = product.technicalSpecs.ramGb.map { option ->
            SelectableOption(option, variants.any { it.matchesIgnoring("ram") && it.ramGb == option && it.stock > 0 })
        }
        val storages = product.technicalSpecs.storageGb.map { option ->
            SelectableOption(option, variants.any { it.matchesIgnoring("storage") && it.storageGb == option && it.stock > 0 })
        }
        val colors = product.technicalSpecs.colors.map { option ->
            SelectableOption(option, variants.any { it.matchesIgnoring("color") && it.color == option && it.stock > 0 })
        }

        val variant = variants.firstOrNull {
            selection.condition?.let { c -> it.condition == c } ?: true &&
                selection.processor?.let { p -> it.processor == p } ?: true &&
                selection.ramGb?.let { r -> it.ramGb == r } ?: true &&
                selection.storageGb?.let { s -> it.storageGb == s } ?: true &&
                selection.color?.let { c -> it.color == c } ?: true
        }

        return ProductOptionsState(
            conditions = conditions,
            processors = processors,
            rams = rams,
            storages = storages,
            colors = colors,
            selectedVariant = variant
        )
    }
}
