package com.funnco.fcmessenger.controller

import com.funnco.fcmessenger.entity.UserEntity
import com.funnco.fcmessenger.interceptor.CurrentUser
import com.funnco.fcmessenger.model.request.RequestUserModel
import com.funnco.fcmessenger.model.response.ResponseTokenHolderModel
import com.funnco.fcmessenger.repository.UserRepository
import com.funnco.fcmessenger.service.JwtService
import com.funnco.fcmessenger.service.UserService
import com.funnco.fcmessenger.utils.HashingUtil
import com.funnco.fcmessenger.utils.UserUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/auth")
class AuthController(
    val userService: UserService,
    val jwtService: JwtService,
    val currentUser: CurrentUser
) {
    private val TAG = "AuthController"
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping("/refresh")
    fun authViaToken(@RequestHeader("Authorization") token: String): ResponseEntity<Void>{
        val currentUser = userRepository.findByToken(token)
        if(currentUser == null){
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.NOT_FOUND, "Can't find user with passed token")
        }
        return ResponseEntity<Void>(null, HttpStatus.OK)
    }

    @GetMapping("/signin")
    fun authViaPassword(@RequestParam email: String, password: String) : ResponseTokenHolderModel?{
        println("$TAG, /user/login: received request with email $email")

        var userPassword = password

        // Checking if user actually exists
        val currentUser = userRepository.findByEmail(email)
        if(currentUser == null) {
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.NOT_FOUND, "Incorrect email or password")
        }

        // Password validation
        userPassword = HashingUtil.hashPassword(password, currentUser!!.userUid!!)
        if(userPassword != currentUser.password){
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.NOT_FOUND, "Incorrect email or password")
        }

        // Generating token, if it didn't exist
        if(currentUser.token == null){
            currentUser.token = jwtService.getTokenForUser(currentUser)
//            currentUser.token =  UUID.fromString(userRepository.generateToken(currentUser.userUid!!))
            userRepository.save(currentUser)
        }
        return ResponseTokenHolderModel(currentUser.token!!.toString())
    }

    @PostMapping("/signup")
    @ResponseBody
    fun register(@RequestBody newUser: RequestUserModel){
        
        try{
            userService.createNewUser(newUser)
        } catch (exception: Exception){
            if(exception is ResponseStatusException){
                throw exception
            } else {
                // TODO ...
            }
        }
    }
}