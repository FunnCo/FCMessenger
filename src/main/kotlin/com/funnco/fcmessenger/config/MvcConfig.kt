package com.funnco.fcmessenger.config

import com.funnco.fcmessenger.interceptor.AuthInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebMvc
@Configuration
class MvcConfig : WebMvcConfigurer {

    @Bean
    fun authInterceptor(): AuthInterceptor {
        return AuthInterceptor()
    }
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor())
        super.addInterceptors(registry)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/resources/image/chat/**")
            .addResourceLocations("file:/root/FCMessenger/resources/image/chat/")
        registry
            .addResourceHandler("/resources/image/user/**")
            .addResourceLocations("file:/root/FCMessenger/resources/image/user/")
    }
}