package com.funnco.fcmessenger.repository

import com.funnco.fcmessenger.entity.ChatEntity
import com.funnco.fcmessenger.entity.ChatMemberEntity
import com.funnco.fcmessenger.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ChatMemberRepository: CrudRepository<ChatMemberEntity, UUID> {
    fun findByRefChatEntityAndRefUserEntity(chatEntity: ChatEntity?, userEntity: UserEntity): ChatMemberEntity?

    fun findByRefUserEntity(userEntity: UserEntity): List<ChatMemberEntity>?
    fun findByRefChatEntity(chatEntity: ChatEntity): List<ChatMemberEntity>?
}