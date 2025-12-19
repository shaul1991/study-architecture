package com.example.mvc.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 상품 엔티티 (Model)
 *
 * MVC 패턴의 Model에 해당하며, 데이터 구조와 비즈니스 로직을 포함합니다.
 */
@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(nullable = false)
    var stock: Int,

    @Column(length = 500)
    var description: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * 비즈니스 로직: 상품이 구매 가능한지 확인
     */
    fun isAvailable(): Boolean = stock > 0

    /**
     * 비즈니스 로직: 재고 업데이트
     * @throws IllegalStateException 재고가 부족한 경우
     */
    fun updateStock(quantity: Int) {
        if (stock + quantity < 0) {
            throw IllegalStateException("재고가 부족합니다. 현재 재고: $stock")
        }
        stock += quantity
    }

    /**
     * 비즈니스 로직: 가격에 세금 포함
     */
    fun priceWithTax(taxRate: BigDecimal = BigDecimal("0.1")): BigDecimal {
        return price * (BigDecimal.ONE + taxRate)
    }
}
