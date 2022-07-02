package com.funnco.fcmessenger.repository

import com.funnco.fcmessenger.entity.ChatEntity
import com.funnco.fcmessenger.entity.MessageEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface MessageRepository: CrudRepository<MessageEntity, UUID> {
    fun findByRefChatEntityOrderByCreationTimeDesc(chatEntity: ChatEntity): List<MessageEntity?>?
}