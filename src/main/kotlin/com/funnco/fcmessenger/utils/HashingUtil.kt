package com.funnco.fcmessenger.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID

object HashingUtil {
    val BASE_SALT =
        "LdepaQ4UYa4m2wsNSMCEeDCALyVbZ9jpGbrhG5fAprDGWCf68f9F5B32L4SDkWawjEbNVbexNuvdUVbKzcLExJZchSaF2MrLuqfKnyUy886gAadGtNvawxYBmQWTHtJMKwhL5GQzk9PEE5DDpZrqXUZ3ycjVRxsLtcgBW82KBvwgeR6H4GCFG6jdEXN7TeBSSstRPVD4xvZ9hgV8nLBGG73mApXB63YUHyg6K4cyVU3pKEqx4gWQ4ZxhhHBMce4d"


    fun hashPassword(password:String, uuid:UUID): String{
        var password = password
        // Handling not hashed password
        if(password.length != 32){
            password = md5Hash(password)
        }
        // Hashing and salting for saving in DB
        return md5Hash(password + uuid.toString() + BASE_SALT)
    }

    fun md5Hash(inputString: String): String{
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(inputString.toByteArray())).toString(16).padStart(32, '0')
    }
}