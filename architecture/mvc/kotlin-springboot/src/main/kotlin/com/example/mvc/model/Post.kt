package com.example.mvc.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 게시글 엔티티
 *
 * 사용자가 작성한 게시글을 나타냅니다.
 */
@Entity
@Table(name = "posts")
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var updatedAt: LocalDateTime? = null,

    @Column(nullable = false)
    var viewCount: Int = 0,

    @Column(nullable = false)
    var published: Boolean = true,

    // 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val images: MutableList<FileEntity> = mutableListOf()
) {
    /**
     * 비즈니스 로직: 조회수 증가
     */
    fun incrementViewCount() {
        viewCount++
    }

    /**
     * 비즈니스 로직: 게시글 수정
     */
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
        this.updatedAt = LocalDateTime.now()
    }

    /**
     * 비즈니스 로직: 댓글 개수 조회
     */
    fun getCommentCount(): Int = comments.size

    /**
     * 비즈니스 로직: 이미지가 있는지 확인
     */
    fun hasImages(): Boolean = images.isNotEmpty()
}
