package com.example.mvc.config

import com.example.mvc.model.*
import com.example.mvc.repository.*
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * 애플리케이션 시작 시 초기 데이터를 생성하는 컴포넌트
 */
@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (userRepository.count() > 0) return

        println("=".repeat(60))
        println("📦 초기 데이터 생성 시작...")
        println("=".repeat(60))

        val users = createUsers()
        println("✅ 사용자 ${users.size}명 생성 완료")

        val posts = createPosts(users)
        println("✅ 게시글 ${posts.size}개 생성 완료")

        val comments = createComments(users, posts)
        println("✅ 댓글 ${comments.size}개 생성 완료")

        val notifications = createNotifications(users, posts, comments)
        println("✅ 알림 ${notifications.size}개 생성 완료")

        println("=".repeat(60))
        println("🎉 초기 데이터 생성 완료!")
        println("=".repeat(60))
        println("\n📌 접속 정보:")
        println("  - 웹 UI: http://localhost:8080/posts")
        println("  - REST API: http://localhost:8080/api/posts")
        println("=".repeat(60))
    }

    private fun createUsers(): List<User> {
        val sampleUsers = listOf(
            User(
                username = "johndoe",
                email = "john@example.com",
                password = "password123",
                displayName = "John Doe",
                bio = "소프트웨어 개발자"
            ),
            User(
                username = "janedoe",
                email = "jane@example.com",
                password = "password123",
                displayName = "Jane Doe",
                bio = "UX 디자이너"
            ),
            User(
                username = "bobsmith",
                email = "bob@example.com",
                password = "password123",
                displayName = "Bob Smith",
                bio = "DevOps 엔지니어"
            )
        )
        return userRepository.saveAll(sampleUsers)
    }

    private fun createPosts(users: List<User>): List<Post> {
        val samplePosts = listOf(
            Post(
                title = "Spring Boot와 Kotlin으로 시작하는 백엔드 개발",
                content = "Spring Boot와 Kotlin은 현대적인 백엔드 개발을 위한 완벽한 조합입니다...",
                author = users[0],
                viewCount = 245
            ),
            Post(
                title = "MVC 패턴 완벽 가이드",
                content = "MVC 패턴은 웹 애플리케이션 개발의 기본입니다...",
                author = users[0],
                viewCount = 189
            ),
            Post(
                title = "JPA 연관관계 매핑 심화",
                content = "JPA에서 엔티티 간의 관계를 올바르게 매핑하는 것은 매우 중요합니다...",
                author = users[0],
                viewCount = 312
            )
        )
        return postRepository.saveAll(samplePosts)
    }

    private fun createComments(users: List<User>, posts: List<Post>): List<Comment> {
        val sampleComments = listOf(
            Comment(
                content = "정말 유익한 글이네요!",
                post = posts[0],
                author = users[1]
            ),
            Comment(
                content = "Spring Boot 공식 문서도 Kotlin 예제를 많이 제공하고 있더라고요.",
                post = posts[0],
                author = users[2]
            ),
            Comment(
                content = "MVC 패턴을 이렇게 명확하게 설명해주셔서 감사합니다.",
                post = posts[1],
                author = users[2]
            )
        )
        return commentRepository.saveAll(sampleComments)
    }

    private fun createNotifications(users: List<User>, posts: List<Post>, comments: List<Comment>): List<Notification> {
        val sampleNotifications = listOf(
            Notification(
                type = NotificationType.NEW_COMMENT,
                message = "${users[1].displayName}님이 회원님의 게시글에 댓글을 남겼습니다.",
                relatedPostId = posts[0].id,
                relatedCommentId = comments[0].id,
                user = users[0]
            ),
            Notification(
                type = NotificationType.SYSTEM,
                message = "환영합니다! 첫 번째 게시글을 작성해보세요.",
                user = users[1],
                isRead = true
            )
        )
        return notificationRepository.saveAll(sampleNotifications)
    }
}
