package com.funnco.fcmessenger.model.request

class RequestChatInvitationModel(
    val chatId: String,
    val invitedChatMembersPhones: List<String>
)