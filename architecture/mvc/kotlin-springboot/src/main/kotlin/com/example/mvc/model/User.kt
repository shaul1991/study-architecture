package com.example.mvc.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 사용자 엔티티
 *
 * 시스템의 사용자를 나타냅니다.
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var username: String,

    @Column(nullable = false, unique = true, length = 100)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(length = 100)
    var displayName: String? = null,

    @Column(length = 500)
    var bio: String? = null,

    @Column
    var profileImageUrl: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var lastLoginAt: LocalDateTime? = null,

    // 관계 매핑
    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    val posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val notifications: MutableList<Notification> = mutableListOf()
) {
    /**
     * 비즈니스 로직: 마지막 로그인 시간 업데이트
     */
    fun updateLastLogin() {
        lastLoginAt = LocalDateTime.now()
    }

    /**
     * 비즈니스 로직: 프로필 이미지가 있는지 확인
     */
    fun hasProfileImage(): Boolean = profileImageUrl != null
}
