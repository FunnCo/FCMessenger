package com.funnco.fcmessenger.entity

import com.funnco.fcmessenger.model.request.RequestUserModel
import com.funnco.fcmessenger.model.response.ResponseUserModel
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "user", schema = "messenger", catalog = "FCMessenger")
class UserEntity {
    @Id
    @Column(name = "user_uid", nullable = false)
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    var userUid: UUID? = null

    @Column(name = "firstname", nullable = false)
    var firstname: String? = null

    @Column(name = "lastname", nullable = false)
    var lastname: String? = null

    @Column(name = "patronymic", nullable = true)
    var patronymic: String? = null

    @Column(name = "avatar_filename", nullable = false)
    var avatarFilename: String? = "default_avatar.png"

    @Column(name = "email", nullable = false)
    var email: String? = null

    @Column(name = "phone", nullable = false)
    var phone: String? = null

    @Column(name = "password", nullable = false)
    var password: String? = null

    @Column(name = "token", nullable = true)
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    var token: UUID? = null


    override fun toString(): String {
        return "UserEntity(userUid=$userUid, firstname=$firstname, lastname=$lastname, patronymic=$patronymic, avatarFilename=$avatarFilename, email=$email, phone=$phone, password=$password, token=$token)"
    }

    fun getRequestModel(): RequestUserModel {
        val request = RequestUserModel()
        request.email = this.email
        request.lastname = this.lastname
        request.firstname = this.firstname
        request.patronymic = this.patronymic
        request.email = this.email
        request.phone = this.phone
        request.password = this.password
        return request
    }

    fun getResponseModel(): ResponseUserModel {
        val response = ResponseUserModel()
        response.firstname = this.firstname
        response.lastname = this.lastname
        response.patronymic = this.patronymic
        response.email = this.email
        response.phone = this.phone
        response.avatarFilename = this.avatarFilename
        return response
    }

}

