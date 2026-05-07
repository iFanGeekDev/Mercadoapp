package com.mercadoapp.data.remote.mapper

import com.mercadoapp.data.remote.dto.OrderDto
import com.mercadoapp.data.remote.dto.OrderItemDto
import com.mercadoapp.data.remote.dto.ProductDto
import com.mercadoapp.data.remote.dto.ProductVariantDto
import com.mercadoapp.data.remote.dto.TechnicalSpecsDto
import com.mercadoapp.data.remote.dto.UserDto
import com.mercadoapp.domain.model.Condition
import com.mercadoapp.domain.model.Order
import com.mercadoapp.domain.model.OrderItem
import com.mercadoapp.domain.model.OrderStatus
import com.mercadoapp.domain.model.Product
import com.mercadoapp.domain.model.ProductVariant
import com.mercadoapp.domain.model.TechnicalSpecs
import com.mercadoapp.domain.model.User
import java.time.Instant

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

// ── Order mappers ─────────────────────────────────────────────────────────────

fun OrderDto.toDomain(): Order = Order(
    id        = id,
    status    = runCatching { OrderStatus.valueOf(status.uppercase()) }.getOrDefault(OrderStatus.PROCESSING),
    createdAt = runCatching { Instant.parse(createdAt) }.getOrDefault(Instant.now()),
    total     = total,
    items     = items.map { it.toDomain() }
)

fun OrderItemDto.toDomain(): OrderItem = OrderItem(
    productId   = productId,
    productName = productName,
    imageUrl    = imageUrl,
    condition   = condition,
    color       = color,
    storageGb   = storageGb,
    quantity    = quantity,
    price       = price
)

// ── Address mappers ───────────────────────────────────────────────────────────

import com.mercadoapp.data.remote.dto.AddressDto
import com.mercadoapp.domain.model.Address

fun AddressDto.toDomain(): Address = Address(
    id = id ?: "",
    alias = alias,
    street = street,
    distrito = districtName,
    provincia = provinceName,
    departamento = departmentName,
    isDefault = isDefault
)

fun Address.toDto(): AddressDto = AddressDto(
    id = id.ifEmpty { null },
    alias = alias,
    street = street,
    departmentName = departamento,
    provinceName = provincia,
    districtName = distrito,
    isDefault = isDefault
)
