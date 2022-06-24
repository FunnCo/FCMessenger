package com.funnco.fcmessenger.entity.composite

import org.hibernate.annotations.Type
import java.util.*
import java.io.Serializable
import javax.persistence.Column

class ChatMemberKey : Serializable{
    @Column(name="chat_uid")
    @Type(type="org.hibernate.type.PostgresUUIDType")
    var chatId : UUID? = null
    @Column(name="user_uid")
    @Type(type="org.hibernate.type.PostgresUUIDType")
    var userId : UUID? = null

}