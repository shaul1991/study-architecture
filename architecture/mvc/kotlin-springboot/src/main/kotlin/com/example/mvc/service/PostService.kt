package com.example.mvc.service

import com.example.mvc.exception.ResourceNotFoundException
import com.example.mvc.model.Post
import com.example.mvc.repository.PostRepository
import com.example.mvc.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 게시글 서비스
 */
@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {
    fun getAllPosts(): List<Post> {
        return postRepository.findAllPublishedOrderByCreatedAtDesc()
    }

    fun getPostById(id: Long): Post {
        return postRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("게시글을 찾을 수 없습니다: ID = $id") }
    }

    fun getPostsByAuthor(authorId: Long): List<Post> {
        val author = userRepository.findById(authorId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $authorId") }
        return postRepository.findByAuthor(author)
    }

    fun searchPostsByTitle(title: String): List<Post> {
        return postRepository.findByTitleContaining(title)
    }

    fun getPopularPosts(): List<Post> {
        return postRepository.findAllPublishedOrderByViewCountDesc()
    }

    @Transactional
    fun createPost(authorId: Long, title: String, content: String, published: Boolean = true): Post {
        val author = userRepository.findById(authorId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $authorId") }

        val post = Post(
            title = title,
            content = content,
            author = author,
            published = published
        )
        return postRepository.save(post)
    }

    @Transactional
    fun updatePost(postId: Long, title: String, content: String): Post {
        val post = getPostById(postId)
        post.update(title, content)
        return post
    }

    @Transactional
    fun deletePost(postId: Long) {
        val post = getPostById(postId)
        postRepository.delete(post)
    }

    @Transactional
    fun incrementViewCount(postId: Long) {
        val post = getPostById(postId)
        post.incrementViewCount()
    }
}
