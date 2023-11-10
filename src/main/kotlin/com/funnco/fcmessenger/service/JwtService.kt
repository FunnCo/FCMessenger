package com.funnco.fcmessenger.service
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.funnco.fcmessenger.entity.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class JwtService {

    final val BASE_SALT =
        "LdepaQ4UYa4m2wsNSMCEeDCALyVbZ9jpGbrhG5fAprDGWCf68f9F5B32L4SDkWawjEbNVbexNuvdUVbKzcLExJZchSaF2MrLuqfKnyUy886gAadGtNvawxYBmQWTHtJMKwhL5GQzk9PEE5DDpZrqXUZ3ycjVRxsLtcgBW82KBvwgeR6H4GCFG6jdEXN7TeBSSstRPVD4xvZ9hgV8nLBGG73mApXB63YUHyg6K4cyVU3pKEqx4gWQ4ZxhhHBMce4d"
    final val TOKEN_BASE =
        "fY7syChN7sEz5DxVettAaPHzmF69mxxZ4FAShpaBkEL3MTTgHwDZ7egHrmnLmwQvjBH9HNSqaZ75bxMJc4CNnGZ3VZAg4hqzUnyZLtA7Gh6KYjseMLyXPFY9593bgvYWDZuRtAVBhBfHKRcNBm3GjjRCq95YBqUed59zqHRQSwwCWhMbYGuEW89Es483qzTSLmfzVa3Wd8uqcGtNjNh37S3SGyZ9pfCUqNhcH6uGUSBweR7mawhZzvQQSGfyXRqD"


    val logger = LoggerFactory.getLogger(this::class.java.simpleName)


    fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

    private fun hashString(input: String, type: String): String {
        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
        return bytes.toHexString()
    }

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
}