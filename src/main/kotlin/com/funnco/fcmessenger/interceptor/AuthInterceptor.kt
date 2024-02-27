package com.funnco.fcmessenger.interceptor

import com.funnco.fcmessenger.interceptor.meta.Authorized
import com.funnco.fcmessenger.service.JwtService
import com.funnco.fcmessenger.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor (val currentUser: CurrentUser, val jwtService: JwtService, val userService: UserService) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        val controllerMethod = handler as HandlerMethod
        val token = request.getHeader("Authorization")

        if(controllerMethod.hasMethodAnnotation(Authorized::class.java)){
            if(jwtService.validateToken(token)){
                currentUser.user = userService.getUserById(jwtService.getUserIdFromToken(token))
            }
        }

        return super.preHandle(request, response, handler)
    }

}