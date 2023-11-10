package com.funnco.fcmessenger.entity

import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.util.UUID


@Entity
@Table(name = "chat", schema = "messenger", catalog = "FCMessenger")
class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "chat_uid", nullable = false)
    var id: UUID? = null

    @Column(name = "chat_name", nullable = false)
    var chatName: String? = null

    @Column(name = "avatar_filename", nullable = false)
    var avatarFileName: String? = null

}

