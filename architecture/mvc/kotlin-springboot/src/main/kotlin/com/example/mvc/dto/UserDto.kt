package com.example.mvc.dto

import com.example.mvc.model.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 사용자 생성 요청 DTO
 */
data class CreateUserDto(
    @field:NotBlank(message = "사용자명은 필수입니다")
    @field:Size(min = 3, max = 50, message = "사용자명은 3~50자 이내여야 합니다")
    val username: String = "",

    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String = "",

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    val password: String = "",

    @field:Size(max = 100, message = "표시 이름은 100자 이내여야 합니다")
    val displayName: String? = null
)

/**
 * 사용자 응답 DTO
 */
data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val displayName: String?,
    val bio: String?,
    val profileImageUrl: String?,
    val postCount: Int,
    val commentCount: Int,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime?
)

/**
 * User Entity를 UserResponse DTO로 변환
 */
fun User.toResponse() = UserResponse(
    id = this.id!!,
    username = this.username,
    email = this.email,
    displayName = this.displayName,
    bio = this.bio,
    profileImageUrl = this.profileImageUrl,
    postCount = this.posts.size,
    commentCount = this.comments.size,
    createdAt = this.createdAt,
    lastLoginAt = this.lastLoginAt
)
