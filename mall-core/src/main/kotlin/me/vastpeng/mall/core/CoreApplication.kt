package me.vastpeng.mall.core

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["me.vastpeng.mall.db", "me.vastpeng.mall.core"])
@MapperScan("me.vastpeng.mall.db.dao")
class CoreApplication

fun main(args: Array<String>) {
    runApplication<CoreApplication>(*args)
}
