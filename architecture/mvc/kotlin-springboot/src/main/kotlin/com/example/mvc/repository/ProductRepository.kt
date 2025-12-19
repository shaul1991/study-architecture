package com.example.mvc.repository

import com.example.mvc.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

/**
 * 상품 리포지토리 (Model - Data Access Layer)
 *
 * Spring Data JPA를 사용하여 데이터베이스 CRUD 작업을 담당합니다.
 */
@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    // Spring Data JPA가 자동으로 구현하는 기본 메서드:
    // - findAll(): 모든 엔티티 조회
    // - findById(id): ID로 엔티티 조회
    // - save(entity): 엔티티 저장 (생성/수정)
    // - deleteById(id): ID로 엔티티 삭제
    // - count(): 전체 개수 조회

    /**
     * 이름으로 상품 검색 (부분 일치)
     * 메서드 이름 규칙으로 쿼리 자동 생성:
     * SELECT * FROM products WHERE name LIKE %:name%
     */
    fun findByNameContaining(name: String): List<Product>

    /**
     * 특정 가격 이하의 상품 조회
     * SELECT * FROM products WHERE price < :price
     */
    fun findByPriceLessThan(price: BigDecimal): List<Product>

    /**
     * 특정 재고 이상의 상품 조회
     * SELECT * FROM products WHERE stock > :stock
     */
    fun findByStockGreaterThan(stock: Int): List<Product>

    /**
     * 가격 범위로 상품 조회
     * SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice
     */
    fun findByPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product>
}
