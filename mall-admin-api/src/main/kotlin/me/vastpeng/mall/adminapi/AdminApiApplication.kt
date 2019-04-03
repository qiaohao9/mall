package me.vastpeng.mall.adminapi

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(scanBasePackages = ["me.vastpeng.mall.db", "me.vastpeng.mall.core", "me.vastpeng.mall.adminapi"])
@MapperScan("me.vastpeng.mall.db.dao")
@EnableTransactionManagement
@EnableScheduling
class AdminApiApplication

fun main(args: Array<String>) {
    SpringApplication.run(AdminApiApplication::class.java, *args)
}
