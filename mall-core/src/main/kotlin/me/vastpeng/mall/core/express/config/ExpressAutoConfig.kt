package me.vastpeng.mall.core.express.config

import me.vastpeng.mall.core.express.`ExpressService.java`
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(ExpressProperties::class)
class ExpressAutoConfiguration(private val properties: ExpressProperties) {

    @Bean
    fun expressService(): `ExpressService.java` {
        val expressService = `ExpressService.java`()
        expressService.properties = properties
        return expressService
    }

}