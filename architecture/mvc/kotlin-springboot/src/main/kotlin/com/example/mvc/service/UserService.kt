package com.example.mvc.service

import com.example.mvc.exception.ResourceNotFoundException
import com.example.mvc.model.User
import com.example.mvc.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 서비스
 */
@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository
) {
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID = $id") }
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username).orElse(null)
    }

    @Transactional
    fun createUser(username: String, email: String, password: String, displayName: String?): User {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("이미 존재하는 사용자명입니다: $username")
        }
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다: $email")
        }

        val user = User(
            username = username,
            email = email,
            password = password, // 실제로는 암호화 필요
            displayName = displayName
        )
        return userRepository.save(user)
    }

    @Transactional
    fun updateLastLogin(userId: Long) {
        val user = getUserById(userId)
        user.updateLastLogin()
    }
}
