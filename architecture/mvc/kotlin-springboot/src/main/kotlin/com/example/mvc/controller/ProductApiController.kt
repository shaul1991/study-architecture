package com.example.mvc.controller

import com.example.mvc.dto.CreateProductDto
import com.example.mvc.dto.ProductResponse
import com.example.mvc.dto.UpdateProductDto
import com.example.mvc.dto.toResponse
import com.example.mvc.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST API 컨트롤러 (Controller)
 *
 * RESTful API를 제공하는 컨트롤러입니다.
 * JSON 형식으로 데이터를 주고받으며, SPA나 모바일 앱의 백엔드로 사용됩니다.
 */
@RestController
@RequestMapping("/api/products")
class ProductApiController(
    private val productService: ProductService
) {
    /**
     * 전체 상품 목록 조회
     * GET /api/products
     */
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts()
            .map { it.toResponse() }
        return ResponseEntity.ok(products)
    }

    /**
     * 특정 상품 조회
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(product.toResponse())
    }

    /**
     * 새 상품 생성
     * POST /api/products
     */
    @PostMapping
    fun createProduct(
        @Valid @RequestBody dto: CreateProductDto
    ): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(dto)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(product.toResponse())
    }

    /**
     * 상품 정보 수정
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody dto: UpdateProductDto
    ): ResponseEntity<ProductResponse> {
        val product = productService.updateProduct(id, dto)
        return ResponseEntity.ok(product.toResponse())
    }

    /**
     * 상품 삭제
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * 상품 검색
     * GET /api/products/search?name=키워드
     */
    @GetMapping("/search")
    fun searchProducts(@RequestParam name: String): ResponseEntity<List<ProductResponse>> {
        val products = productService.searchProductsByName(name)
            .map { it.toResponse() }
        return ResponseEntity.ok(products)
    }
}
