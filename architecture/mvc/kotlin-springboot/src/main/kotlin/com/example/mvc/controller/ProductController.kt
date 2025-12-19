package com.example.mvc.controller

import com.example.mvc.dto.CreateProductDto
import com.example.mvc.dto.UpdateProductDto
import com.example.mvc.service.ProductService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * 웹 MVC 컨트롤러 (Controller)
 *
 * 전통적인 MVC 패턴의 Controller로, HTML 뷰를 반환합니다.
 * Thymeleaf 템플릿 엔진을 사용하여 서버 사이드 렌더링을 수행합니다.
 */
@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {
    /**
     * 상품 목록 페이지
     * GET /products
     */
    @GetMapping
    fun listProducts(model: Model): String {
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "products/list"  // templates/products/list.html
    }

    /**
     * 상품 상세 페이지
     * GET /products/{id}
     */
    @GetMapping("/{id}")
    fun showProduct(@PathVariable id: Long, model: Model): String {
        val product = productService.getProductById(id)
        model.addAttribute("product", product)
        return "products/detail"  // templates/products/detail.html
    }

    /**
     * 상품 등록 폼 페이지
     * GET /products/new
     */
    @GetMapping("/new")
    fun newProductForm(model: Model): String {
        model.addAttribute("product", CreateProductDto())
        model.addAttribute("isEdit", false)
        return "products/form"  // templates/products/form.html
    }

    /**
     * 상품 등록 처리
     * POST /products
     */
    @PostMapping
    fun createProduct(
        @Valid @ModelAttribute("product") dto: CreateProductDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        // 검증 오류가 있으면 폼 페이지로 돌아감
        if (bindingResult.hasErrors()) {
            return "products/form"
        }

        val product = productService.createProduct(dto)
        redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.")
        return "redirect:/products/${product.id}"
    }

    /**
     * 상품 수정 폼 페이지
     * GET /products/{id}/edit
     */
    @GetMapping("/{id}/edit")
    fun editProductForm(@PathVariable id: Long, model: Model): String {
        val product = productService.getProductById(id)
        val dto = UpdateProductDto(
            name = product.name,
            price = product.price,
            stock = product.stock,
            description = product.description
        )
        model.addAttribute("productId", id)
        model.addAttribute("product", dto)
        model.addAttribute("isEdit", true)
        return "products/form"
    }

    /**
     * 상품 수정 처리
     * POST /products/{id}
     */
    @PostMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @ModelAttribute("product") dto: UpdateProductDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id)
            model.addAttribute("isEdit", true)
            return "products/form"
        }

        productService.updateProduct(id, dto)
        redirectAttributes.addFlashAttribute("message", "상품이 수정되었습니다.")
        return "redirect:/products/$id"
    }

    /**
     * 상품 삭제 처리
     * POST /products/{id}/delete
     */
    @PostMapping("/{id}/delete")
    fun deleteProduct(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        productService.deleteProduct(id)
        redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.")
        return "redirect:/products"
    }
}
