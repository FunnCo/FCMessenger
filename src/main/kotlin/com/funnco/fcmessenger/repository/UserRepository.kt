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
    fun findByToken(token: String): UserEntity?
    fun findByPhone(phone: String): UserEntity?
}