package com.funnco.fcmessenger.entity

import org.hibernate.annotations.Type
import java.sql.Timestamp
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "message", schema = "messenger", catalog = "FCMessenger")
class MessageEntity {
    @Id
    @Column(name = "message_uid", nullable = false)
    @Type(type="org.hibernate.type.PostgresUUIDType")
    var messageUid: UUID? = null

    @Column(name = "message_content", nullable = true)
    var messageContent: String? = null

    @Column(name = "message_extra", nullable = true)
    var messageExtra: String? = null

    @Column(name = "creation_time", nullable = false)
    var creationTime: Timestamp? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_uid", referencedColumnName = "chat_uid")
    var refChatEntity: ChatEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", referencedColumnName = "user_uid")
    var refUserEntity: UserEntity? = null

}

