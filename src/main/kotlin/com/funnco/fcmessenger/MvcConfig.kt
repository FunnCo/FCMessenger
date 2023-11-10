package com.funnco.fcmessenger

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebMvc
class MvcConfig: WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/resources/image/chat/**")
            .addResourceLocations("file:/root/FCMessenger/resources/image/chat/")
        registry
            .addResourceHandler("/resources/image/user/**")
            .addResourceLocations("file:/root/FCMessenger/resources/image/user/")
    }
}