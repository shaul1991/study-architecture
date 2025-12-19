package com.example.mvc.exception

/**
 * 상품을 찾을 수 없을 때 발생하는 예외
 */
class ProductNotFoundException(message: String) : RuntimeException(message)
