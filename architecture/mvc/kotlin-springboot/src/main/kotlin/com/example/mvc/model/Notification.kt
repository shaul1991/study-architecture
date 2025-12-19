package com.example.mvc.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 알림 엔티티
 *
 * 사용자에게 전달되는 알림을 나타냅니다.
 */
@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var type: NotificationType,

    @Column(nullable = false, length = 200)
    var message: String,

    @Column
    var relatedPostId: Long? = null,

    @Column
    var relatedCommentId: Long? = null,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var readAt: LocalDateTime? = null,

    // 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
) {
    /**
     * 비즈니스 로직: 알림을 읽음으로 표시
     */
    fun markAsRead() {
        if (!isRead) {
            isRead = true
            readAt = LocalDateTime.now()
        }
    }

    /**
     * 비즈니스 로직: 알림이 최근 것인지 확인 (24시간 이내)
     */
    fun isRecent(): Boolean {
        return createdAt.isAfter(LocalDateTime.now().minusDays(1))
    }
}

/**
 * 알림 타입 열거형
 */
enum class NotificationType {
    NEW_COMMENT,      // 새 댓글
    NEW_REPLY,        // 대댓글
    POST_LIKED,       // 게시글 좋아요 (확장 가능)
    MENTION,          // 언급
    SYSTEM            // 시스템 알림
}
