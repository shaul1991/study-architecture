package com.example.mvc.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal

/**
 * 상품 수정 요청 DTO
 */
data class UpdateProductDto(
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(min = 2, max = 100, message = "상품명은 2~100자 이내여야 합니다")
    val name: String = "",

    @field:NotNull(message = "가격은 필수입니다")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    val price: BigDecimal = BigDecimal.ZERO,

    @field:NotNull(message = "재고는 필수입니다")
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    val stock: Int = 0,

    @field:Size(max = 500, message = "설명은 500자 이내여야 합니다")
    val description: String? = null
)
