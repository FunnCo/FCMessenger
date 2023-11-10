package com.funnco.fcmessenger.entity.composite

import org.hibernate.annotations.Type
import java.util.*
import java.io.Serializable
import jakarta.persistence.Column

class ChatMemberKey : Serializable{
    @Column(name="chat_uid")
    var chatId : UUID? = null
    @Column(name="user_uid")
    var userId : UUID? = null

}