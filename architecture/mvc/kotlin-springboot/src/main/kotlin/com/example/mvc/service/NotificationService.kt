package com.example.mvc.service

import com.example.mvc.exception.ResourceNotFoundException
import com.example.mvc.model.Notification
import com.example.mvc.model.NotificationType
import com.example.mvc.repository.NotificationRepository
import com.example.mvc.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 알림 서비스
 */
@Service
@Transactional(readOnly = true)
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {
    fun getNotificationsByUser(userId: Long): List<Notification> {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $userId") }
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
    }

    fun getUnreadNotifications(userId: Long): List<Notification> {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $userId") }
        return notificationRepository.findByUserAndIsReadFalse(user)
    }

    fun getUnreadCount(userId: Long): Long {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $userId") }
        return notificationRepository.countByUserAndIsReadFalse(user)
    }

    @Transactional
    fun createNotification(
        userId: Long,
        type: NotificationType,
        message: String,
        relatedPostId: Long? = null,
        relatedCommentId: Long? = null
    ): Notification {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $userId") }

        val notification = Notification(
            type = type,
            message = message,
            relatedPostId = relatedPostId,
            relatedCommentId = relatedCommentId,
            user = user
        )
        return notificationRepository.save(notification)
    }

    @Transactional
    fun markAsRead(notificationId: Long): Notification {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { ResourceNotFoundException("알림을 찾을 수 없습니다: ID = $notificationId") }
        notification.markAsRead()
        return notification
    }

    @Transactional
    fun markAllAsRead(userId: Long) {
        val notifications = getUnreadNotifications(userId)
        notifications.forEach { it.markAsRead() }
    }
}
