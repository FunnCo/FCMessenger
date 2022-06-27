package com.funnco.fcmessenger

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.io.File

@Configuration
@EnableWebMvc
class MvcConfig: WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/resources/image/chat/**")
            .addResourceLocations("file:/usr/local/bin/server-exec/resources/chat/")
        registry
            .addResourceHandler("/resources/image/user/**")
            .addResourceLocations("file:/usr/local/bin/server-exec/resources/user/")
    }
}