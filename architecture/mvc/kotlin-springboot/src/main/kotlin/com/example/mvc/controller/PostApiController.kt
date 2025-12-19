package com.example.mvc.controller

import com.example.mvc.dto.*
import com.example.mvc.service.PostService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 게시글 REST API 컨트롤러
 *
 * JSON 형식으로 데이터를 주고받습니다.
 */
@RestController
@RequestMapping("/api/posts")
class PostApiController(
    private val postService: PostService
) {
    /**
     * 전체 게시글 목록 조회
     * GET /api/posts
     */
    @GetMapping
    fun getAllPosts(): ResponseEntity<List<PostSummaryResponse>> {
        val posts = postService.getAllPosts()
            .map { it.toSummaryResponse() }
        return ResponseEntity.ok(posts)
    }

    /**
     * 인기 게시글 조회 (조회수 기준)
     * GET /api/posts/popular
     */
    @GetMapping("/popular")
    fun getPopularPosts(): ResponseEntity<List<PostSummaryResponse>> {
        val posts = postService.getPopularPosts()
            .map { it.toSummaryResponse() }
        return ResponseEntity.ok(posts)
    }

    /**
     * 특정 게시글 조회
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    fun getPost(@PathVariable id: Long): ResponseEntity<PostResponse> {
        val post = postService.getPostById(id)
        postService.incrementViewCount(id) // 조회수 증가
        return ResponseEntity.ok(post.toResponse())
    }

    /**
     * 새 게시글 작성
     * POST /api/posts
     */
    @PostMapping
    fun createPost(
        @Valid @RequestBody dto: CreatePostDto,
        @RequestHeader("X-User-Id", required = false) userId: Long?
    ): ResponseEntity<PostResponse> {
        // 실제로는 인증 토큰에서 사용자 ID를 추출해야 함
        val authorId = userId ?: 1L
        val post = postService.createPost(authorId, dto.title, dto.content, dto.published)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(post.toResponse())
    }

    /**
     * 게시글 수정
     * PUT /api/posts/{id}
     */
    @PutMapping("/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @Valid @RequestBody dto: UpdatePostDto
    ): ResponseEntity<PostResponse> {
        val post = postService.updatePost(id, dto.title, dto.content)
        return ResponseEntity.ok(post.toResponse())
    }

    /**
     * 게시글 삭제
     * DELETE /api/posts/{id}
     */
    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<Void> {
        postService.deletePost(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * 게시글 검색
     * GET /api/posts/search?title=keyword
     */
    @GetMapping("/search")
    fun searchPosts(@RequestParam title: String): ResponseEntity<List<PostSummaryResponse>> {
        val posts = postService.searchPostsByTitle(title)
            .map { it.toSummaryResponse() }
        return ResponseEntity.ok(posts)
    }

    /**
     * 특정 사용자의 게시글 조회
     * GET /api/posts/author/{authorId}
     */
    @GetMapping("/author/{authorId}")
    fun getPostsByAuthor(@PathVariable authorId: Long): ResponseEntity<List<PostSummaryResponse>> {
        val posts = postService.getPostsByAuthor(authorId)
            .map { it.toSummaryResponse() }
        return ResponseEntity.ok(posts)
    }
}
