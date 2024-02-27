package com.funnco.fcmessenger.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.funnco.fcmessenger.entity.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class JwtService {

    final val TOKEN_BASE =
        "fY7syChN7sEz5DxVettAaPHzmF69mxxZ4FAShpaBkEL3MTTgHwDZ7egHrmnLmwQvjBH9HNSqaZ75bxMJc4CNnGZ3VZAg4hqzUnyZLtA7Gh6KYjseMLyXPFY9593bgvYWDZuRtAVBhBfHKRcNBm3GjjRCq95YBqUed59zqHRQSwwCWhMbYGuEW89Es483qzTSLmfzVa3Wd8uqcGtNjNh37S3SGyZ9pfCUqNhcH6uGUSBweR7mawhZzvQQSGfyXRqD"

//    fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
//
//    private fun hashString(input: String, type: String): String {
//        val bytes = MessageDigest
//            .getInstance(type)
//            .digest(input.toByteArray())
//        return bytes.toHexString()
//    }

    fun getTokenForUser(userDTO: UserEntity): String {
        val algorithm = Algorithm.HMAC256(TOKEN_BASE)
        val createTime = Instant.now()
        val expirationTime = createTime.plus(7, ChronoUnit.DAYS)

        return JWT.create()
            .withIssuer("auth_service")
            .withSubject(userDTO.userUid!!.toString())
            .withIssuedAt(Date.from(createTime))
            .withExpiresAt(Date.from(expirationTime))
            .sign(algorithm)
    }

    fun validateToken(token: String): Boolean {
        val decodedToken = JWT.decode(token)
        try {
            JWT.require(Algorithm.HMAC256(TOKEN_BASE)).withIssuer("auth_service").build().verify(decodedToken)
        } catch (_: JWTVerificationException) {
            return false
        }
        return decodedToken.expiresAt.after(Date(System.currentTimeMillis()))
    }

    fun getUserIdFromToken(token: String): String {
        val decodedToken = JWT.decode(token)
        return decodedToken.subject
    }
}