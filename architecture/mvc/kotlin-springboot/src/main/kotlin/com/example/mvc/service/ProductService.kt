package com.example.mvc.service

import com.example.mvc.dto.CreateProductDto
import com.example.mvc.dto.UpdateProductDto
import com.example.mvc.exception.ProductNotFoundException
import com.example.mvc.model.Product
import com.example.mvc.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 상품 서비스 (Model - Business Logic Layer)
 *
 * MVC 패턴의 Model에 해당하며, 비즈니스 로직을 담당합니다.
 * Controller와 Repository 사이에서 중재 역할을 수행합니다.
 */
@Service
@Transactional(readOnly = true)  // 기본적으로 읽기 전용 트랜잭션
class ProductService(
    private val productRepository: ProductRepository
) {
    /**
     * 모든 상품 조회
     */
    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    /**
     * ID로 상품 조회
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     */
    fun getProductById(id: Long): Product {
        return productRepository.findById(id)
            .orElseThrow { ProductNotFoundException("상품을 찾을 수 없습니다: ID = $id") }
    }

    /**
     * 새 상품 생성
     */
    @Transactional  // 쓰기 작업이므로 readOnly = false
    fun createProduct(dto: CreateProductDto): Product {
        val product = Product(
            name = dto.name,
            price = dto.price,
            stock = dto.stock,
            description = dto.description
        )

        return productRepository.save(product)
    }

    /**
     * 상품 정보 수정
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     */
    @Transactional
    fun updateProduct(id: Long, dto: UpdateProductDto): Product {
        val product = getProductById(id)

        // 더티 체킹(Dirty Checking)으로 자동 업데이트
        product.name = dto.name
        product.price = dto.price
        product.stock = dto.stock
        product.description = dto.description

        return product
    }

    /**
     * 상품 삭제
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     */
    @Transactional
    fun deleteProduct(id: Long) {
        val product = getProductById(id)
        productRepository.delete(product)
    }

    /**
     * 이름으로 상품 검색
     */
    fun searchProductsByName(name: String): List<Product> {
        return productRepository.findByNameContaining(name)
    }
}
