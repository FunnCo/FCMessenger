package com.funnco.fcmessenger.model.response

class ResponseChatModel(
    val chatId: String,
    val chatName: String,
    val avatarFilepath: String,
    val chatMembers: List<ResponseUserModel>
)