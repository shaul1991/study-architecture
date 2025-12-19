package com.example.mvc.dto

import com.example.mvc.model.Product
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 상품 응답 DTO
 *
 * REST API에서 클라이언트에게 반환하는 데이터 형식입니다.
 * Entity를 직접 노출하지 않고 필요한 필드만 선택적으로 제공합니다.
 */
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val stock: Int,
    val description: String?,
    val createdAt: LocalDateTime,
    val available: Boolean
)

/**
 * Product Entity를 ProductResponse DTO로 변환하는 Extension Function
 */
fun Product.toResponse() = ProductResponse(
    id = this.id!!,
    name = this.name,
    price = this.price,
    stock = this.stock,
    description = this.description,
    createdAt = this.createdAt,
    available = this.isAvailable()
)
