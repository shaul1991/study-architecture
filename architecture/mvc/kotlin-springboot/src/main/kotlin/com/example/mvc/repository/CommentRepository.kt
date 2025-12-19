package com.example.mvc.repository

import com.example.mvc.model.Comment
import com.example.mvc.model.Post
import com.example.mvc.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * 댓글 리포지토리
 */
@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByPost(post: Post): List<Comment>
    fun findByAuthor(author: User): List<Comment>

    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.parent IS NULL ORDER BY c.createdAt DESC")
    fun findTopLevelCommentsByPost(post: Post): List<Comment>

    fun countByPost(post: Post): Long
}
