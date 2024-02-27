package com.funnco.fcmessenger.interceptor

import com.funnco.fcmessenger.entity.UserEntity
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component


@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentUser {
    var user: UserEntity? = null
}