package com.funnco.fcmessenger.utils

import com.funnco.fcmessenger.entity.UserEntity
import com.funnco.fcmessenger.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*


object RestControllerUtil {

    enum class HTTPResponseStatus(val code: HttpStatus){
        BAD_REQUEST(HttpStatus.BAD_REQUEST),
        NOT_FOUND(HttpStatus.NOT_FOUND),
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
        OK(HttpStatus.OK),
        CREATED(HttpStatus.CREATED)
    }

    fun throwException(status: HTTPResponseStatus, message: String) {
        throw ResponseStatusException(
            status.code, message
        )
    }

    fun getUserByToken(userRepository: UserRepository, token: String): UserEntity {
        val currentUser = userRepository.findByToken(token)
        if (currentUser == null) {
            throwException(HTTPResponseStatus.UNAUTHORIZED, "Invalid token")
        }
        return currentUser!!
    }
}