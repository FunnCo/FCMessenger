package com.funnco.fcmessenger.controller

import com.funnco.fcmessenger.entity.UserEntity
import com.funnco.fcmessenger.model.request.RequestUserModel
import com.funnco.fcmessenger.model.response.ResponseTokenHolderModel
import com.funnco.fcmessenger.repository.UserRepository
import com.funnco.fcmessenger.service.JwtService
import com.funnco.fcmessenger.utils.HashingUtil
import com.funnco.fcmessenger.utils.RestControllerUtil
import com.funnco.fcmessenger.utils.UserUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AuthController(
    val jwtService: JwtService
) {
    private val TAG = "AuthController"
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping("/user/login/token")
    fun authViaToken(@RequestHeader("Authorization") token: String): ResponseEntity<Void>{
        println("$TAG, /user/login/token: received request with token $token")

        val currentUser = userRepository.findByToken(token)
        if(currentUser == null){
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.NOT_FOUND, "Can't find user with passed token")
        }
        return ResponseEntity<Void>(null, HttpStatus.OK)
    }

    @GetMapping("/user/login")
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

    @PostMapping("/user/register")
    fun register(@RequestBody newUser: RequestUserModel){
        println("$TAG, /user/register: received request for creating new user $newUser")

        // Check if user is already exists
        var currentUser = userRepository.findByEmailOrPhone(newUser.email, newUser.phone)
        if(currentUser != null){
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.BAD_REQUEST, "User with same mail or phone already exists")
        }

        // Checking if all necessary fields are not empty
        if(newUser.password == null || newUser.email == null || newUser.firstname == null || newUser.lastname == null || newUser.phone == null) {
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.BAD_REQUEST, "Not all necessary fields are passed")
        }

        // Phone validation
        if(UserUtil.isNumberValid(newUser.phone!!)){
            RestControllerUtil.throwException(RestControllerUtil.HTTPResponseStatus.BAD_REQUEST, "Invalid phone number")
        }

        // Creating new user
        currentUser = UserEntity()
        currentUser.userUid = UUID.randomUUID()

        // Hashing and salting for saving in DB
        currentUser.password = HashingUtil.hashPassword(newUser.password!!, currentUser.userUid!!)

        // Setting rest of the fields
        currentUser.token = null
        currentUser.email = newUser.email
        currentUser.phone = newUser.phone
        currentUser.firstname = newUser.firstname
        currentUser.lastname = newUser.lastname
        currentUser.patronymic = newUser.patronymic

        // Saving user in DB
        userRepository.save(currentUser)
    }
}