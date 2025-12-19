package com.example.mvc.config

import com.example.mvc.model.Product
import com.example.mvc.repository.ProductRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * 애플리케이션 시작 시 초기 데이터를 생성하는 컴포넌트
 *
 * ApplicationRunner를 구현하여 Spring Boot 애플리케이션이 시작된 후 실행됩니다.
 * 데모 목적으로 샘플 상품 데이터를 자동으로 생성합니다.
 */
@Component
class DataInitializer(
    private val productRepository: ProductRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        // 이미 데이터가 있으면 초기화하지 않음
        if (productRepository.count() > 0) {
            return
        }

        // 샘플 상품 데이터 생성
        val sampleProducts = listOf(
            Product(
                name = "노트북",
                price = BigDecimal("1500000"),
                stock = 10,
                description = "고성능 노트북으로 개발 작업에 최적화되어 있습니다."
            ),
            Product(
                name = "무선 마우스",
                price = BigDecimal("35000"),
                stock = 50,
                description = "편안한 그립감의 무선 마우스입니다."
            ),
            Product(
                name = "기계식 키보드",
                price = BigDecimal("120000"),
                stock = 25,
                description = "청축 기계식 키보드로 타건감이 뛰어납니다."
            ),
            Product(
                name = "27인치 모니터",
                price = BigDecimal("450000"),
                stock = 15,
                description = "4K 해상도를 지원하는 고급 모니터입니다."
            ),
            Product(
                name = "USB-C 허브",
                price = BigDecimal("55000"),
                stock = 100,
                description = "다양한 포트를 제공하는 멀티 허브입니다."
            ),
            Product(
                name = "노트북 거치대",
                price = BigDecimal("28000"),
                stock = 0,  // 품절 상태 데모
                description = "알루미늄 소재의 고급 노트북 거치대입니다."
            )
        )

        productRepository.saveAll(sampleProducts)

        println("✅ 초기 데이터 생성 완료: ${sampleProducts.size}개의 상품이 등록되었습니다.")
    }
}
