package com.example.mvc.repository

import com.example.mvc.model.Post
import com.example.mvc.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * 게시글 리포지토리
 */
@Repository
interface PostRepository : JpaRepository<Post, Long> {
    fun findByAuthor(author: User): List<Post>
    fun findByPublishedTrue(): List<Post>
    fun findByTitleContaining(title: String): List<Post>

    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.createdAt DESC")
    fun findAllPublishedOrderByCreatedAtDesc(): List<Post>

    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.viewCount DESC")
    fun findAllPublishedOrderByViewCountDesc(): List<Post>
}
