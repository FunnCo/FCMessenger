package com.funnco.fcmessenger.repository

import com.funnco.fcmessenger.entity.UserEntity
import org.hibernate.annotations.Type
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.query.Procedure
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface UserRepository : CrudRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailOrPhone(email: String?, phone: String?): UserEntity?
    fun findByToken(token: UUID): UserEntity?
    fun findByPhone(phone: String): UserEntity?

    @Query(nativeQuery = true, value = "SELECT * FROM messenger.create_token(CAST (:userUUID AS UUID))")
    fun generateToken(@Param("userUUID") userUUID: UUID): String

    @Query(nativeQuery = true, value = "CALL messenger.empty_token(CAST (:userUUID AS UUID))")
    fun clearToken(@Param("userUUID") userUUID: UUID): Void
}