package com.funnco.fcmessenger.entity

import com.funnco.fcmessenger.entity.composite.ChatMemberKey
import java.util.*
import jakarta.persistence.*

@Entity
@Table(name = "chat_member", schema = "messenger", catalog = "FCMessenger")
class ChatMemberEntity {
    @EmbeddedId
    var key: ChatMemberKey? = null

    @Column(name = "last_check", nullable = false)
    var lastCheck: java.sql.Timestamp? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_uid")
    var refChatEntity: ChatEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_uid")
    var refUserEntity: UserEntity? = null


}
