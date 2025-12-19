package com.example.mvc.dto

import com.example.mvc.model.Comment
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 댓글 생성 요청 DTO
 */
data class CreateCommentDto(
    @field:NotBlank(message = "댓글 내용은 필수입니다")
    @field:Size(min = 1, max = 1000, message = "댓글은 1~1000자 이내여야 합니다")
    val content: String = "",

    val parentId: Long? = null
)

/**
 * 댓글 수정 요청 DTO
 */
data class UpdateCommentDto(
    @field:NotBlank(message = "댓글 내용은 필수입니다")
    @field:Size(min = 1, max = 1000, message = "댓글은 1~1000자 이내여야 합니다")
    val content: String = ""
)

/**
 * 댓글 응답 DTO
 */
data class CommentResponse(
    val id: Long,
    val content: String,
    val authorId: Long,
    val authorUsername: String,
    val authorDisplayName: String?,
    val postId: Long,
    val parentId: Long?,
    val replyCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

/**
 * Comment Entity를 CommentResponse DTO로 변환
 */
fun Comment.toResponse() = CommentResponse(
    id = this.id!!,
    content = this.content,
    authorId = this.author.id!!,
    authorUsername = this.author.username,
    authorDisplayName = this.author.displayName,
    postId = this.post.id!!,
    parentId = this.parent?.id,
    replyCount = this.getReplyCount(),
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
