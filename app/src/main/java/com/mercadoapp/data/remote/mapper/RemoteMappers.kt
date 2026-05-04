package com.mercadoapp.data.remote.mapper

import com.mercadoapp.data.remote.dto.ProductDto
import com.mercadoapp.data.remote.dto.ProductVariantDto
import com.mercadoapp.data.remote.dto.TechnicalSpecsDto
import com.mercadoapp.data.remote.dto.UserDto
import com.mercadoapp.domain.model.Condition
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.model.ProductVariant
import com.mercadoapp.domain.model.TechnicalSpecs
import com.mercadoapp.domain.model.User

// ── Product mappers ────────────────────────────────────────────────────────────

fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    imageUrl = imageUrl,
    shortDescription = shortDescription,
    technicalSpecs = technicalSpecs.toDomain(),
    variants = variants.map { it.toDomain() },
    isOffer = isOffer,
    isNewArrival = isNewArrival
)

fun TechnicalSpecsDto.toDomain(): TechnicalSpecs = TechnicalSpecs(
    processor = processor,
    ramGb = ramGb,
    storageGb = storageGb,
    colors = colors
)

fun ProductVariantDto.toDomain(): ProductVariant = ProductVariant(
    condition = Condition.valueOf(condition.uppercase()),
    processor = processor,
    ramGb = ramGb,
    storageGb = storageGb,
    color = color,
    price = price,
    stock = stock
)

// ── User mapper ───────────────────────────────────────────────────────────────

fun UserDto.toDomain(): User = User(
    id = id,
    email = email,
    name = name,
    avatarUrl = avatarUrl
)
