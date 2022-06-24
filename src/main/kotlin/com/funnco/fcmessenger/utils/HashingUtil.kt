package com.funnco.fcmessenger.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID

object HashingUtil {

    fun hashPassword(password:String, uuid: UUID): String{
        var password = password
        // Handling not hashed password
        if(password.length != 32){
            password = HashingUtil.md5Hash(password)
        }
        // Hashing and salting for saving in DB
        return md5Hash(password + (uuid.toString() + "4_Secrets_In_Sugar"))
    }

    fun md5Hash(inputString: String): String{
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(inputString.toByteArray())).toString(16).padStart(32, '0')
    }
}