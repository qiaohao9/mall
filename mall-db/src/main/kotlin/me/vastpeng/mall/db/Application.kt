package me.vastpeng.mall.db

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["me.vastpeng.mall.db"])
@MapperScan("me.vastpeng.mall.db.dao")
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
