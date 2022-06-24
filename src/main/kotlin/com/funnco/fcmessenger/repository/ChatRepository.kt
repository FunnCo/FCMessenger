package com.funnco.fcmessenger.repository

import com.funnco.fcmessenger.entity.ChatEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ChatRepository: CrudRepository<ChatEntity, UUID> {
    fun findByChatName(chatname: String?): ChatEntity?
}