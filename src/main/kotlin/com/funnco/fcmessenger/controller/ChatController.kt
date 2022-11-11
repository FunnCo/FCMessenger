package com.funnco.fcmessenger.controller

import com.funnco.fcmessenger.entity.ChatEntity
import com.funnco.fcmessenger.entity.ChatMemberEntity
import com.funnco.fcmessenger.entity.MessageEntity
import com.funnco.fcmessenger.entity.UserEntity
import com.funnco.fcmessenger.entity.composite.ChatMemberKey
import com.funnco.fcmessenger.model.request.RequestChatCreationModel
import com.funnco.fcmessenger.model.request.RequestChatInvitationModel
import com.funnco.fcmessenger.model.request.RequestMessageModel
import com.funnco.fcmessenger.model.response.ResponseChatModel
import com.funnco.fcmessenger.model.response.ResponseMessageModel
import com.funnco.fcmessenger.model.response.ResponseUserModel
import com.funnco.fcmessenger.repository.ChatMemberRepository
import com.funnco.fcmessenger.repository.ChatRepository
import com.funnco.fcmessenger.repository.MessageRepository
import com.funnco.fcmessenger.repository.UserRepository
import com.funnco.fcmessenger.utils.HashingUtil
import com.funnco.fcmessenger.utils.RestControllerUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.sql.Timestamp
import java.util.*

@RestController
class ChatController {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var chatRepository: ChatRepository

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var chatMemberRepository: ChatMemberRepository

    @PostMapping("/chat/create")
    fun createChat(
        @RequestHeader("Authorization") token: String,
        @RequestBody requestedChat: RequestChatCreationModel
    ): ResponseChatModel {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        if (requestedChat.chatMembersPhones.distinct().size == 1) {
            return createPrivateChat(requestedChat, currentUser)
        }
        return createGroupChat(requestedChat, currentUser)
    }

    private fun createPrivateChat(requestedChat: RequestChatCreationModel, chatCreator: UserEntity): ResponseChatModel {
        var newChat = ChatEntity()
        val secondUser = userRepository.findByPhone(requestedChat.chatMembersPhones[0])
        if (secondUser == null) {
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.NOT_FOUND, "Second user not found")
        }
        newChat.chatName = chatCreator.userUid.toString() + "___" + secondUser!!.userUid.toString()
        val anotherVariantOfName = secondUser.userUid.toString() + "___" + chatCreator.userUid.toString()

        if (chatRepository.findByChatName(newChat.chatName) != null || chatRepository.findByChatName(anotherVariantOfName) != null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.BAD_REQUEST,
                "This chat already exists"
            )
        }

        newChat.avatarFileName = "defaultChatAvatar.png"

        chatRepository.save(newChat)
        newChat = chatRepository.findByChatName(newChat.chatName)!!

        val creatorEntry = ChatMemberEntity()
        creatorEntry.key = ChatMemberKey()
        creatorEntry.key!!.chatId = newChat.id
        creatorEntry.key!!.userId = chatCreator.userUid
        creatorEntry.refChatEntity = newChat
        creatorEntry.refUserEntity = chatCreator
        creatorEntry.lastCheck = Timestamp(System.currentTimeMillis())
        chatMemberRepository.save(creatorEntry)

        val secondUserEntry = ChatMemberEntity()
        secondUserEntry.key = ChatMemberKey()
        secondUserEntry.key!!.chatId = newChat.id
        secondUserEntry.key!!.userId = secondUser.userUid
        secondUserEntry.refChatEntity = newChat
        secondUserEntry.refUserEntity = secondUser
        secondUserEntry.lastCheck = Timestamp(System.currentTimeMillis())
        chatMemberRepository.save(secondUserEntry)

        val responseUsers = listOf<ResponseUserModel>(chatCreator.getResponseModel(), secondUser.getResponseModel())
        return ResponseChatModel(
            newChat.id!!.toString(),
            secondUser.lastname!!,
            newChat.avatarFileName!!,
            responseUsers,
            null,
            true
        )
    }

    private fun createGroupChat(requestedChat: RequestChatCreationModel, chatCreator: UserEntity): ResponseChatModel {
        if (chatRepository.findByChatName(requestedChat.chatName) != null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.BAD_REQUEST,
                "This chat already exists"
            )
        }

        var newChat = ChatEntity()
        newChat.avatarFileName = "defaultChatAvatar.png"
        newChat.chatName = requestedChat.chatName
        chatRepository.save(newChat)
        newChat = chatRepository.findByChatName(newChat.chatName)!!

        val creatorEntry = ChatMemberEntity()
        creatorEntry.key = ChatMemberKey()
        creatorEntry.key!!.chatId = newChat.id
        creatorEntry.key!!.userId = chatCreator.userUid
        creatorEntry.refChatEntity = newChat
        creatorEntry.refUserEntity = chatCreator
        creatorEntry.lastCheck = Timestamp(System.currentTimeMillis())


        val listOfMembers = mutableListOf<ResponseUserModel>(chatCreator.getResponseModel())
        val listOfEntries = mutableListOf<ChatMemberEntity>()
        listOfEntries.add(creatorEntry)
        for (phone in requestedChat.chatMembersPhones) {
            val member = userRepository.findByPhone(phone)
            if (member == null) {
                RestControllerUtil.throwException(
                    RestControllerUtil.HTTPResponseStatus.NOT_FOUND,
                    "One of the users is not found"
                )
            }
            val memberEntry = ChatMemberEntity()
            memberEntry.key = ChatMemberKey()
            memberEntry.key!!.chatId = newChat.id
            memberEntry.key!!.userId = member!!.userUid
            memberEntry.refChatEntity = newChat
            memberEntry.refUserEntity = member
            memberEntry.lastCheck = Timestamp(System.currentTimeMillis())
            listOfEntries.add(memberEntry)
            listOfMembers.add(member.getResponseModel())
        }
        chatMemberRepository.saveAll(listOfEntries)
        return ResponseChatModel(
            newChat.id!!.toString(),
            newChat.chatName!!,
            newChat.avatarFileName!!,
            listOfMembers,
            null,
            false
        )
    }

    @PostMapping("/chat/post/message")
    fun postMessage(
        @RequestHeader("Authorization") token: String,
        @RequestBody requestedMessage: RequestMessageModel
    ) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        val refChat = chatRepository.findByIdOrNull(UUID.fromString(requestedMessage.chatId))
        if (refChat == null || chatMemberRepository.findByRefChatEntityAndRefUserEntity(refChat, currentUser) == null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.NOT_FOUND,
                "Chat with this id is not found in your list of chats"
            )
        }
        if (requestedMessage.messageContent.isBlank()) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.BAD_REQUEST,
                "Message can't be empty"
            )
        }
        val message = MessageEntity()
        message.messageContent = requestedMessage.messageContent
        message.messageExtra = null
        message.messageUid = UUID.randomUUID()
        message.refChatEntity = refChat
        message.refUserEntity = currentUser
        message.creationTime = Timestamp(System.currentTimeMillis())
        messageRepository.save(message)
    }

    @GetMapping("/chat/get/messages")
    fun getMessages(
        @RequestHeader("Authorization") token: String,
        @RequestParam chatId: String
    ): List<ResponseMessageModel> {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        val currentChat = chatRepository.findByIdOrNull(UUID.fromString(chatId))
        val currentChatMemberEntity = chatMemberRepository.findByRefChatEntityAndRefUserEntity(currentChat, currentUser)
        if (currentChatMemberEntity == null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.NOT_FOUND,
                "Chat with this id is not found in your list of chats"
            )
        }

        val messageEntities = messageRepository.findByRefChatEntityOrderByCreationTimeDesc(currentChat!!)!!
        val response = mutableListOf<ResponseMessageModel>()
        for (item in messageEntities) {
            response.add(
                ResponseMessageModel(
                    item!!.creationTime!!,
                    item.messageContent!!,
                    item.refUserEntity!!.getResponseModel(),
                    item.messageExtra
                )
            )
        }
        currentChatMemberEntity!!.lastCheck = Timestamp(System.currentTimeMillis())
        chatMemberRepository.save(currentChatMemberEntity)
        return response
    }

    @PutMapping("/chat/invite")
    fun inviteToChat(
        @RequestHeader("Authorization") token: String,
        @RequestBody requestedChat: RequestChatInvitationModel
    ) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        val currentChat = chatRepository.findByIdOrNull(UUID.fromString(requestedChat.chatId))
        val currentChatMemberEntity = chatMemberRepository.findByRefChatEntityAndRefUserEntity(currentChat, currentUser)
        if (currentChatMemberEntity == null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.NOT_FOUND,
                "Chat with this id is not found in your list of chats"
            )
        }

        val listOfNewMembers = mutableListOf<ChatMemberEntity>()
        for (phone in requestedChat.invitedChatMembersPhones) {
            val member = userRepository.findByPhone(phone) ?: continue
            val memberEntry = ChatMemberEntity()
            memberEntry.key = ChatMemberKey()
            memberEntry.key!!.chatId = currentChat!!.id
            memberEntry.key!!.userId = member!!.userUid
            memberEntry.refChatEntity = currentChat
            memberEntry.refUserEntity = member
            memberEntry.lastCheck = Timestamp(System.currentTimeMillis())
            listOfNewMembers.add(memberEntry)
        }
        if(listOfNewMembers.isEmpty()){
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.NOT_FOUND,
                "No new members are passed or their phones are incorrect")
        }
        chatMemberRepository.saveAll(listOfNewMembers)
    }

    @PutMapping("/chat/leave")
    fun leaveChat(
        @RequestHeader("Authorization") token: String,
        @RequestHeader("ChatId") chatId: String
    ) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        val currentChat = chatRepository.findByIdOrNull(UUID.fromString(chatId))
        val currentChatMemberEntity = chatMemberRepository.findByRefChatEntityAndRefUserEntity(currentChat, currentUser)
        if (currentChatMemberEntity == null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.NOT_FOUND,
                "Chat with this id is not found in your list of chats"
            )
        }
        chatMemberRepository.delete(currentChatMemberEntity!!)
    }

    @GetMapping("/chat/my")
    fun getUserChats(
        @RequestHeader("Authorization") token: String,
    ): List<ResponseChatModel> {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        val userChatsEntries = chatMemberRepository.findByRefUserEntity(currentUser)
        val resultList = mutableListOf<ResponseChatModel>()
        for (item in userChatsEntries!!) {

            val members = chatMemberRepository.findByRefChatEntity(item.refChatEntity!!)!!
            val responseUsers = mutableListOf<ResponseUserModel>()
            for (user in members) {
                responseUsers.add(user.refUserEntity!!.getResponseModel())
            }

            var isChatPrivate = false
            var responseChatAvatar = item.refChatEntity!!.avatarFileName!!
            var responseChatName = item.refChatEntity!!.chatName!!
            if (responseUsers.size == 2 && responseChatName.matches("^[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}___[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}\$".toRegex())) {
                if(responseUsers[0].phone == currentUser.phone){
                    responseChatName = responseUsers[1].lastname!! + " " + responseUsers[1].firstname
                    responseChatAvatar = responseUsers[1].avatarFilename!!
                } else {
                    responseChatName = responseUsers[0].lastname!! + " " + responseUsers[0].firstname
                    responseChatAvatar = responseUsers[0].avatarFilename!!
                }
                isChatPrivate = true
            }
            var lastMessage: ResponseMessageModel?
            try {
                lastMessage =
                    messageRepository.findByRefChatEntityOrderByCreationTimeDesc(item.refChatEntity!!)?.first()
                        ?.parseToResponse()
            } catch (e: NoSuchElementException) {
                lastMessage = null
            }



            val chat = ResponseChatModel(
                chatId = item.refChatEntity!!.id!!.toString(),
                chatName = responseChatName,
                avatarFilepath = responseChatAvatar,
                chatMembers = responseUsers,
                lastMessage,
                isChatPrivate
            )
            resultList.add(chat)
        }
        return resultList
    }

    @PostMapping("/chat/change/avatar")
    fun chatChangeAbout(
        @RequestHeader("Authorization") token: String,
        @RequestParam("image") image: MultipartFile,
        @RequestParam chatId: String
    ) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token as String)
        image.transferTo(File("/usr/local/bin/server-exec/resources/chat/chat_${HashingUtil.md5Hash(currentUser.phone!!)}_avatar.png"))

        val currentChat = chatRepository.findByIdOrNull(UUID.fromString(chatId))
        currentChat!!.avatarFileName = "chat_${HashingUtil.md5Hash(currentUser.phone!!)}_avatar.png"
        chatRepository.save(currentChat)
    }

    @PutMapping("/chat/change/name")
    fun chatChangeName(
        @RequestHeader("Authorization") token: String,
        @RequestParam newName: String,
        @RequestParam chatId: String
    ) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token as String)
        val currentChat = chatRepository.findByIdOrNull(UUID.fromString(chatId))
        currentChat!!.chatName = newName
        chatRepository.save(currentChat)
    }
}