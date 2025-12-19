package com.example.mvc.dto

import com.example.mvc.model.Post
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 게시글 생성 요청 DTO
 */
data class CreatePostDto(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(min = 2, max = 200, message = "제목은 2~200자 이내여야 합니다")
    val title: String = "",

    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다")
    val content: String = "",

    val published: Boolean = true
)

/**
 * 게시글 수정 요청 DTO
 */
data class UpdatePostDto(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(min = 2, max = 200, message = "제목은 2~200자 이내여야 합니다")
    val title: String = "",

    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다")
    val content: String = ""
)

/**
 * 게시글 응답 DTO
 */
data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorId: Long,
    val authorUsername: String,
    val authorDisplayName: String?,
    val viewCount: Int,
    val commentCount: Int,
    val published: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

/**
 * 게시글 목록용 요약 DTO
 */
data class PostSummaryResponse(
    val id: Long,
    val title: String,
    val contentPreview: String,
    val authorUsername: String,
    val authorDisplayName: String?,
    val viewCount: Int,
    val commentCount: Int,
    val createdAt: LocalDateTime
)

/**
 * Post Entity를 PostResponse DTO로 변환
 */
fun Post.toResponse() = PostResponse(
    id = this.id!!,
    title = this.title,
    content = this.content,
    authorId = this.author.id!!,
    authorUsername = this.author.username,
    authorDisplayName = this.author.displayName,
    viewCount = this.viewCount,
    commentCount = this.getCommentCount(),
    published = this.published,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Post Entity를 PostSummaryResponse DTO로 변환
 */
fun Post.toSummaryResponse() = PostSummaryResponse(
    id = this.id!!,
    title = this.title,
    contentPreview = if (this.content.length > 150) this.content.substring(0, 150) + "..." else this.content,
    authorUsername = this.author.username,
    authorDisplayName = this.author.displayName,
    viewCount = this.viewCount,
    commentCount = this.getCommentCount(),
    createdAt = this.createdAt
)
