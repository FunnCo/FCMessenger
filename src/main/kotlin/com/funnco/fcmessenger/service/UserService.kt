package com.funnco.fcmessenger.service

import com.funnco.fcmessenger.entity.UserEntity
import com.funnco.fcmessenger.model.request.RequestUserModel
import com.funnco.fcmessenger.repository.UserRepository
import com.funnco.fcmessenger.utils.RestControllerUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserService {

    private fun isEmailValid(email: String): Boolean{
        return email.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+\$".toRegex())
    }

    private fun isPhoneValid(phone: String): Boolean{
        val clearPhone = phone
            .replace("(", "")
            .replace(")", "")
            .replace("-", "")
            .replace(" ", "")

        return clearPhone.matches("^\\+7\\d{10}\$".toRegex())
    }

    @Autowired
    lateinit var userRepository: UserRepository

    fun createNewUser(newUser: RequestUserModel) {
        // Checking if all necessary fields are not empty
        if (newUser.password == null ||
            newUser.email == null ||
            newUser.firstname == null ||
            newUser.lastname == null ||
            newUser.phone == null
        ) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Some of the fields are empty")
        }

        // Phone and email validation
        if(!(isEmailValid(newUser.email!!) && isPhoneValid(newUser.phone!!))){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone are invalid")
        }

        // Unique fields validating
        if (userRepository.existsByEmailOrPhone(newUser.email!!, newUser.email!!)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User with such email or phone already exists")
        }

        // Creating entity and saving into db
        userRepository.save(UserEntity(newUser))
    }

    fun getUserById(id: UUID): UserEntity? {
        return userRepository.findByIdOrNull(id)
    }

    fun getUserById(id: String): UserEntity? {
        return userRepository.findByIdOrNull(UUID.fromString(id))
    }

}