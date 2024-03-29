package com.funnco.fcmessenger.controller

import com.funnco.fcmessenger.model.request.RequestUserModel
import com.funnco.fcmessenger.model.response.ResponseUserModel
import com.funnco.fcmessenger.repository.UserRepository
import com.funnco.fcmessenger.utils.HashingUtil
import com.funnco.fcmessenger.utils.RestControllerUtil
import com.funnco.fcmessenger.utils.UserUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@RestController
class UserController {

    private val TAG = "UserController"

    @Autowired
    private lateinit var userRepository: UserRepository

    @PostMapping("/user/leave_all")
    fun invalidateCurrentToken(@RequestHeader("Authorization") token: String) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        try {
            currentUser.token = null
            userRepository.save(currentUser)
        } catch (_: Exception) {
        }
    }

    @PutMapping("/user/change/about")
    fun updateUserInfo(@RequestHeader("Authorization") token: String, @RequestBody newInfo: RequestUserModel) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token)
        if (newInfo.phone != null && UserUtil.isNumberValid(newInfo.phone!!)) {
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.BAD_REQUEST, "Invalid phone number")
        }
        if (newInfo.phone != null && userRepository.findByPhone(newInfo.phone!!) != null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.BAD_REQUEST,
                "New phone is already used by someone else"
            )
        }
        currentUser.phone = newInfo.phone ?: currentUser.phone
        currentUser.patronymic = newInfo.patronymic ?: currentUser.patronymic
        currentUser.lastname = newInfo.lastname ?: currentUser.lastname
        currentUser.firstname = newInfo.firstname ?: currentUser.firstname
        currentUser.email = newInfo.email ?: currentUser.email
        if (newInfo.password != null) {
            currentUser.password = HashingUtil.hashPassword(newInfo.password!!, currentUser.userUid!!)
        }
        userRepository.save(currentUser)
    }

    @GetMapping("/user/info")
    fun getUserInfo(@RequestHeader("Authorization") token: String, @RequestParam phone: String?): ResponseUserModel {
        val authorizedUser = RestControllerUtil.getUserByToken(userRepository, token)
        val requestedUser = if (phone == null || phone == "null") {
            authorizedUser.getResponseModel()
        } else {
            if (UserUtil.isNumberValid(phone)) {
                RestControllerUtil.throwException(
                    RestControllerUtil.HTTPResponseStatus.BAD_REQUEST,
                    "Invalid phone number"
                )
            }
            userRepository.findByPhone(phone)?.getResponseModel()
        }

        if (requestedUser == null) {
            RestControllerUtil.throwException(
                RestControllerUtil.HTTPResponseStatus.NOT_FOUND,
                "User with requested phone is not found"
            )
        }
        val responseUser = ResponseUserModel()
        responseUser.email = requestedUser!!.email
        responseUser.phone = requestedUser.phone
        responseUser.firstname = requestedUser.firstname
        responseUser.lastname = requestedUser.lastname
        responseUser.patronymic = requestedUser.patronymic
        responseUser.avatarFilename = requestedUser.avatarFilename

        return responseUser
    }

    @PostMapping("/user/change/avatar")
    fun userChangeAvatar(@RequestHeader("Authorization") token: String, @RequestParam("image") image: MultipartFile) {
        val currentUser = RestControllerUtil.getUserByToken(userRepository, token as String)
        image.transferTo(File("/root/FCMessenger/resources/image/user/user_${HashingUtil.md5Hash(currentUser.phone!!)}_avatar.png"))

        currentUser.avatarFilename = "user_${HashingUtil.md5Hash(currentUser.phone!!)}_avatar.png"
        userRepository.save(currentUser)
    }
}
