package com.example.mvc.service

import com.example.mvc.exception.ResourceNotFoundException
import com.example.mvc.model.Comment
import com.example.mvc.repository.CommentRepository
import com.example.mvc.repository.PostRepository
import com.example.mvc.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 댓글 서비스
 */
@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {
    fun getCommentsByPost(postId: Long): List<Comment> {
        val post = postRepository.findById(postId)
            .orElseThrow { ResourceNotFoundException("게시글을 찾을 수 없습니다: ID = $postId") }
        return commentRepository.findTopLevelCommentsByPost(post)
    }

    fun getCommentById(id: Long): Comment {
        return commentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("댓글을 찾을 수 없습니다: ID = $id") }
    }

    @Transactional
    fun createComment(postId: Long, authorId: Long, content: String, parentId: Long? = null): Comment {
        val post = postRepository.findById(postId)
            .orElseThrow { ResourceNotFoundException("게시글을 찾을 수 없습니다: ID = $postId") }
        val author = userRepository.findById(authorId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $authorId") }

        val parent = parentId?.let {
            commentRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("부모 댓글을 찾을 수 없습니다: ID = $it") }
        }

        val comment = Comment(
            content = content,
            post = post,
            author = author,
            parent = parent
        )
        return commentRepository.save(comment)
    }

    @Transactional
    fun updateComment(commentId: Long, content: String): Comment {
        val comment = getCommentById(commentId)
        comment.update(content)
        return comment
    }

    @Transactional
    fun deleteComment(commentId: Long) {
        val comment = getCommentById(commentId)
        commentRepository.delete(comment)
    }

    fun getCommentCount(postId: Long): Long {
        val post = postRepository.findById(postId)
            .orElseThrow { ResourceNotFoundException("게시글을 찾을 수 없습니다: ID = $postId") }
        return commentRepository.countByPost(post)
    }
}
