package com.funnco.fcmessenger.model.response

import java.sql.Timestamp

class ResponseMessageModel(
    val creationTime: Timestamp,
    val messageContent: String,
    val author: ResponseUserModel,
    val attachmentFilepath: String?
)