# Kotlin + Spring Boot MVC 구현 예제

> 실제 동작하는 Kotlin과 Spring Boot를 사용한 MVC 패턴 구현 예제

---

## 📋 목차

1. [프로젝트 개요](#-프로젝트-개요)
2. [프로젝트 구조](#-프로젝트-구조)
3. [기술 스택](#-기술-스택)
4. [실행 방법](#-실행-방법)
5. [주요 기능](#-주요-기능)
6. [코드 설명](#-코드-설명)
7. [API 엔드포인트](#-api-엔드포인트)
8. [학습 포인트](#-학습-포인트)

---

## 🎯 프로젝트 개요

이 프로젝트는 **상품 관리 시스템**을 통해 Spring Boot MVC 패턴을 학습하기 위한 예제입니다.

**주요 기능:**
- ✅ 상품 목록 조회 (웹 UI + REST API)
- ✅ 상품 상세 조회
- ✅ 상품 등록
- ✅ 상품 수정
- ✅ 상품 삭제
- ✅ Thymeleaf 템플릿 기반 웹 UI
- ✅ RESTful API 제공

**학습 목표:**
- MVC 패턴의 각 컴포넌트 역할 이해
- Spring Boot의 자동 구성 활용
- Kotlin + Spring Boot 통합
- JPA를 통한 데이터베이스 연동
- Thymeleaf 템플릿 엔진 사용
- REST API 설계

---

## 📁 프로젝트 구조

```
kotlin-springboot/
├── build.gradle.kts                    # Gradle 빌드 설정 (Kotlin DSL)
├── settings.gradle.kts                 # Gradle 프로젝트 설정
│
└── src/
    └── main/
        ├── kotlin/
        │   └── com/example/mvc/
        │       ├── MvcApplication.kt           # Spring Boot Main 클래스
        │       │
        │       ├── controller/                 # Controller 계층
        │       │   ├── ProductController.kt    # 웹 MVC 컨트롤러
        │       │   └── ProductApiController.kt # REST API 컨트롤러
        │       │
        │       ├── service/                    # Service 계층 (Model - 비즈니스 로직)
        │       │   └── ProductService.kt
        │       │
        │       ├── repository/                 # Repository 계층 (Model - 데이터 접근)
        │       │   └── ProductRepository.kt
        │       │
        │       ├── model/                      # Entity (Model - 데이터 구조)
        │       │   └── Product.kt
        │       │
        │       ├── dto/                        # DTO (Data Transfer Object)
        │       │   ├── CreateProductDto.kt
        │       │   ├── UpdateProductDto.kt
        │       │   └── ProductResponse.kt
        │       │
        │       └── exception/                  # 예외 처리
        │           └── ProductNotFoundException.kt
        │
        └── resources/
            ├── application.yml                 # Spring Boot 설정
            │
            ├── templates/                      # Thymeleaf 템플릿 (View)
            │   ├── layout/
            │   │   └── default.html            # 공통 레이아웃
            │   │
            │   └── products/
            │       ├── list.html               # 상품 목록
            │       ├── detail.html             # 상품 상세
            │       └── form.html               # 상품 등록/수정 폼
            │
            └── static/                         # 정적 리소스
                ├── css/
                │   └── style.css
                └── js/
                    └── app.js
```

---

## 🛠️ 기술 스택

### Backend
- **Kotlin** 1.9+
- **Spring Boot** 3.2+
- **Spring Web MVC**
- **Spring Data JPA**
- **H2 Database** (인메모리 DB)
- **Bean Validation**

### View
- **Thymeleaf** 3.1+ (템플릿 엔진)
- **Bootstrap** 5 (CSS 프레임워크)

### Build Tool
- **Gradle** 8+ (Kotlin DSL)

---

## 🚀 실행 방법

### 1. 프로젝트 빌드

```bash
cd architecture/mvc/kotlin-springboot
./gradlew build
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 3. 브라우저에서 접속

- **웹 UI**: http://localhost:8080/products
- **REST API**: http://localhost:8080/api/products
- **H2 Console**: http://localhost:8080/h2-console

### 4. H2 Database 접속 정보

```yaml
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (공백)
```

---

## ✨ 주요 기능

### 1. 웹 UI (Thymeleaf)

#### 상품 목록 페이지
- URL: `GET /products`
- 전체 상품 목록 표시
- 상세 보기, 수정, 삭제 버튼

#### 상품 등록 페이지
- URL: `GET /products/new`
- 상품 등록 폼
- 입력 검증 (Bean Validation)

#### 상품 수정 페이지
- URL: `GET /products/{id}/edit`
- 기존 상품 정보로 폼 채우기
- 수정 후 저장

### 2. REST API

| HTTP 메서드 | 엔드포인트 | 설명 |
|-------------|-----------|------|
| GET | /api/products | 전체 상품 목록 조회 |
| GET | /api/products/{id} | 특정 상품 조회 |
| POST | /api/products | 새 상품 생성 |
| PUT | /api/products/{id} | 상품 정보 수정 |
| DELETE | /api/products/{id} | 상품 삭제 |

---

## 📖 코드 설명

### 1. Model - Entity

```kotlin
@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false)
    var price: BigDecimal,

    @Column(nullable = false)
    var stock: Int,

    @Column(length = 500)
    var description: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    // 비즈니스 로직
    fun isAvailable(): Boolean = stock > 0

    fun updateStock(quantity: Int) {
        if (stock + quantity < 0) {
            throw IllegalStateException("재고가 부족합니다")
        }
        stock += quantity
    }
}
```

**특징:**
- `@Entity`: JPA 엔티티 선언
- `@Id`, `@GeneratedValue`: 기본 키 자동 생성
- `data class`: Kotlin의 데이터 클래스 활용
- 비즈니스 메서드 포함 (Rich Domain Model)

---

### 2. Model - Repository

```kotlin
@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    // Spring Data JPA가 자동으로 구현을 제공
    // - findAll()
    // - findById()
    // - save()
    // - deleteById()

    // 커스텀 쿼리 메서드
    fun findByNameContaining(name: String): List<Product>
    fun findByPriceLessThan(price: BigDecimal): List<Product>
    fun findByStockGreaterThan(stock: Int): List<Product>
}
```

**특징:**
- `JpaRepository` 상속으로 기본 CRUD 메서드 자동 제공
- 메서드 이름 규칙으로 쿼리 자동 생성
- 별도 구현 없이 인터페이스만 선언

---

### 3. Model - Service

```kotlin
@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository
) {
    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    fun getProductById(id: Long): Product {
        return productRepository.findById(id)
            .orElseThrow { ProductNotFoundException("상품을 찾을 수 없습니다: $id") }
    }

    @Transactional
    fun createProduct(dto: CreateProductDto): Product {
        val product = Product(
            name = dto.name,
            price = dto.price,
            stock = dto.stock,
            description = dto.description
        )
        return productRepository.save(product)
    }

    @Transactional
    fun updateProduct(id: Long, dto: UpdateProductDto): Product {
        val product = getProductById(id)
        product.name = dto.name
        product.price = dto.price
        product.stock = dto.stock
        product.description = dto.description
        return productRepository.save(product)
    }

    @Transactional
    fun deleteProduct(id: Long) {
        val product = getProductById(id)
        productRepository.delete(product)
    }
}
```

**특징:**
- `@Service`: 비즈니스 로직 계층 표시
- `@Transactional`: 트랜잭션 관리
- 생성자 주입으로 Repository 의존성 주입
- 비즈니스 로직과 예외 처리

---

### 4. Controller - 웹 MVC

```kotlin
@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping
    fun listProducts(model: Model): String {
        val products = productService.getAllProducts()
        model.addAttribute("products", products)
        return "products/list"  // templates/products/list.html
    }

    @GetMapping("/{id}")
    fun showProduct(@PathVariable id: Long, model: Model): String {
        val product = productService.getProductById(id)
        model.addAttribute("product", product)
        return "products/detail"
    }

    @GetMapping("/new")
    fun newProductForm(model: Model): String {
        model.addAttribute("product", CreateProductDto())
        return "products/form"
    }

    @PostMapping
    fun createProduct(
        @Valid @ModelAttribute("product") dto: CreateProductDto,
        bindingResult: BindingResult
    ): String {
        if (bindingResult.hasErrors()) {
            return "products/form"
        }

        productService.createProduct(dto)
        return "redirect:/products"
    }

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
        return "products/form"
    }

    @PostMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @ModelAttribute("product") dto: UpdateProductDto,
        bindingResult: BindingResult
    ): String {
        if (bindingResult.hasErrors()) {
            return "products/form"
        }

        productService.updateProduct(id, dto)
        return "redirect:/products"
    }

    @PostMapping("/{id}/delete")
    fun deleteProduct(@PathVariable id: Long): String {
        productService.deleteProduct(id)
        return "redirect:/products"
    }
}
```

**특징:**
- `@Controller`: 웹 MVC 컨트롤러
- View 이름 반환 (Thymeleaf 템플릿)
- `Model`에 데이터 추가
- Form 데이터 바인딩과 검증

---

### 5. Controller - REST API

```kotlin
@RestController
@RequestMapping("/api/products")
class ProductApiController(
    private val productService: ProductService
) {
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts()
            .map { it.toResponse() }
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(product.toResponse())
    }

    @PostMapping
    fun createProduct(
        @Valid @RequestBody dto: CreateProductDto
    ): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(dto)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(product.toResponse())
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody dto: UpdateProductDto
    ): ResponseEntity<ProductResponse> {
        val product = productService.updateProduct(id, dto)
        return ResponseEntity.ok(product.toResponse())
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}

// Extension function for DTO conversion
private fun Product.toResponse() = ProductResponse(
    id = this.id!!,
    name = this.name,
    price = this.price,
    stock = this.stock,
    description = this.description,
    createdAt = this.createdAt
)
```

**특징:**
- `@RestController`: REST API 컨트롤러
- JSON 응답 자동 변환
- HTTP 상태 코드 명시
- Extension function으로 DTO 변환

---

### 6. DTO (Data Transfer Object)

```kotlin
// 상품 생성 요청 DTO
data class CreateProductDto(
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(min = 2, max = 100, message = "상품명은 2~100자 이내여야 합니다")
    val name: String = "",

    @field:NotNull(message = "가격은 필수입니다")
    @field:DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
    val price: BigDecimal = BigDecimal.ZERO,

    @field:NotNull(message = "재고는 필수입니다")
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    val stock: Int = 0,

    @field:Size(max = 500, message = "설명은 500자 이내여야 합니다")
    val description: String? = null
)

// 상품 응답 DTO
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val stock: Int,
    val description: String?,
    val createdAt: LocalDateTime
)
```

**특징:**
- Bean Validation 어노테이션으로 입력 검증
- Entity와 분리하여 API 응답 제어
- 불변 객체 (`val`) 권장

---

### 7. View - Thymeleaf 템플릿

```html
<!-- templates/products/list.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>상품 목록</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h1>상품 목록</h1>

    <a th:href="@{/products/new}" class="btn btn-primary mb-3">새 상품 등록</a>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>ID</th>
            <th>상품명</th>
            <th>가격</th>
            <th>재고</th>
            <th>작업</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="product : ${products}">
            <td th:text="${product.id}"></td>
            <td>
                <a th:href="@{/products/{id}(id=${product.id})}" th:text="${product.name}"></a>
            </td>
            <td th:text="${#numbers.formatCurrency(product.price)}"></td>
            <td th:text="${product.stock}"></td>
            <td>
                <a th:href="@{/products/{id}/edit(id=${product.id})}" class="btn btn-sm btn-warning">수정</a>
                <form th:action="@{/products/{id}/delete(id=${product.id})}" method="post" style="display:inline;">
                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('삭제하시겠습니까?')">삭제</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>
```

**Thymeleaf 주요 문법:**
- `th:each`: 반복문
- `th:text`: 텍스트 출력
- `th:href`: URL 생성
- `${...}`: 변수 표현식
- `@{...}`: URL 표현식
- `#numbers.formatCurrency()`: 유틸리티 객체

---

## 🌐 API 엔드포인트

### REST API 사용 예제

#### 1. 전체 상품 조회
```bash
curl -X GET http://localhost:8080/api/products
```

**응답:**
```json
[
  {
    "id": 1,
    "name": "노트북",
    "price": 1500000.00,
    "stock": 10,
    "description": "고성능 노트북",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

#### 2. 상품 생성
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "무선 마우스",
    "price": 35000,
    "stock": 50,
    "description": "편안한 무선 마우스"
  }'
```

**응답:** `201 Created`
```json
{
  "id": 2,
  "name": "무선 마우스",
  "price": 35000.00,
  "stock": 50,
  "description": "편안한 무선 마우스",
  "createdAt": "2024-01-15T14:20:00"
}
```

#### 3. 상품 수정
```bash
curl -X PUT http://localhost:8080/api/products/2 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "무선 마우스 프로",
    "price": 45000,
    "stock": 45,
    "description": "업그레이드된 무선 마우스"
  }'
```

#### 4. 상품 삭제
```bash
curl -X DELETE http://localhost:8080/api/products/2
```

**응답:** `204 No Content`

---

## 📚 학습 포인트

### 1. MVC 패턴 이해
- **Model**: `Product` (Entity), `ProductService`, `ProductRepository`
- **View**: Thymeleaf 템플릿 (`list.html`, `form.html`)
- **Controller**: `ProductController` (웹), `ProductApiController` (API)

### 2. Spring Boot 핵심 개념
- **의존성 주입 (DI)**: 생성자 주입 방식
- **자동 구성**: `@SpringBootApplication`
- **어노테이션 기반**: `@Controller`, `@Service`, `@Repository`

### 3. Spring Data JPA
- `JpaRepository` 상속으로 CRUD 자동 구현
- 메서드 이름 규칙으로 쿼리 생성
- `@Transactional`로 트랜잭션 관리

### 4. Bean Validation
- `@NotBlank`, `@NotNull`, `@Size` 등
- 입력 검증 자동화
- `@Valid`와 `BindingResult`

### 5. REST API 설계
- RESTful 원칙 준수
- HTTP 메서드와 상태 코드 활용
- DTO 패턴으로 요청/응답 분리

### 6. Kotlin + Spring Boot
- `data class` 활용
- Null 안정성 (`?`, `!!`)
- Extension function
- 간결한 문법

---

## 🔍 추가 학습 자료

### 다음 단계
1. **예외 처리 강화**
   - `@ControllerAdvice`로 전역 예외 처리
   - 커스텀 에러 응답

2. **페이징과 정렬**
   - `Pageable` 인터페이스 활용
   - `PagingAndSortingRepository`

3. **검색 기능**
   - Query DSL
   - Specification API

4. **보안**
   - Spring Security
   - JWT 인증

5. **테스트**
   - Unit Test (MockMvc)
   - Integration Test

---

## 📝 라이선스

이 프로젝트는 학습 목적으로 작성되었습니다.
