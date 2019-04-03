package me.vastpeng.mall.core.storage.config

import me.vastpeng.mall.core.storage.StorageService
import me.vastpeng.mall.core.storage.storager.LocalStorage
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(StorageProperties::class)
class StorageAutoConfiguration(private val properties: StorageProperties) {

    @Bean
    fun storageService(): StorageService {
        val storageService = StorageService()
        val active = this.properties.active
        storageService.active = active
        when (active) {
            "local" -> storageService.storage = localStorage()
            else -> throw RuntimeException("当前存储模式 $active 不支持")
        }

        return storageService
    }

    @Bean
    fun localStorage(): LocalStorage {
        val localStorage = LocalStorage()
        val local = this.properties.local
        localStorage.address = local.address
        localStorage.storagePath = local.storagePath
        return localStorage
    }


}
