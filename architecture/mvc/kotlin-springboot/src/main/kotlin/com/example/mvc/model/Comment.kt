package com.example.mvc.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 댓글 엔티티
 *
 * 게시글에 달린 댓글을 나타냅니다.
 */
@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var updatedAt: LocalDateTime? = null,

    // 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,

    // 대댓글 지원 (선택적)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    val replies: MutableList<Comment> = mutableListOf()
) {
    /**
     * 비즈니스 로직: 댓글 수정
     */
    fun update(content: String) {
        this.content = content
        this.updatedAt = LocalDateTime.now()
    }

    /**
     * 비즈니스 로직: 대댓글인지 확인
     */
    fun isReply(): Boolean = parent != null

    /**
     * 비즈니스 로직: 대댓글 개수 조회
     */
    fun getReplyCount(): Int = replies.size
}
