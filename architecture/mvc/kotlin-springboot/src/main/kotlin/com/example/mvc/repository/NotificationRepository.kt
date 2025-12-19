package com.example.mvc.repository

import com.example.mvc.model.Notification
import com.example.mvc.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * 알림 리포지토리
 */
@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUser(user: User): List<Notification>
    fun findByUserAndIsReadFalse(user: User): List<Notification>
    fun countByUserAndIsReadFalse(user: User): Long

    @Query("SELECT n FROM Notification n WHERE n.user = :user ORDER BY n.createdAt DESC")
    fun findByUserOrderByCreatedAtDesc(user: User): List<Notification>
}
