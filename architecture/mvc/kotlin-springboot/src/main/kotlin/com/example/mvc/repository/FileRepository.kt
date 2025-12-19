package com.example.mvc.repository

import com.example.mvc.model.FileEntity
import com.example.mvc.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 파일 리포지토리
 */
@Repository
interface FileRepository : JpaRepository<FileEntity, Long> {
    fun findByPost(post: Post): List<FileEntity>
    fun findByStoredFileName(storedFileName: String): FileEntity?
}
