package com.example.mvc.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 파일/이미지 엔티티
 *
 * 게시글에 첨부된 파일이나 이미지를 나타냅니다.
 */
@Entity
@Table(name = "files")
data class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var originalFileName: String,

    @Column(nullable = false)
    var storedFileName: String,

    @Column(nullable = false)
    var fileUrl: String,

    @Column(nullable = false)
    var fileSize: Long, // bytes

    @Column(nullable = false, length = 100)
    var contentType: String,

    @Column(nullable = false)
    val uploadedAt: LocalDateTime = LocalDateTime.now(),

    // 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post
) {
    /**
     * 비즈니스 로직: 이미지 파일인지 확인
     */
    fun isImage(): Boolean = contentType.startsWith("image/")

    /**
     * 비즈니스 로직: 파일 크기를 MB 단위로 반환
     */
    fun getFileSizeInMB(): Double = fileSize / (1024.0 * 1024.0)

    /**
     * 비즈니스 로직: 파일 확장자 추출
     */
    fun getFileExtension(): String {
        val lastDotIndex = originalFileName.lastIndexOf('.')
        return if (lastDotIndex > 0) {
            originalFileName.substring(lastDotIndex + 1)
        } else {
            ""
        }
    }
}
