package com.funnco.fcmessenger.utils

import com.funnco.fcmessenger.entity.UserEntity
import java.io.IOException

object UserUtil {
    fun isNumberValid (inputPhone: String): Boolean{
        return !inputPhone.matches("^\\+7\\d{10}$".toRegex())
    }
}