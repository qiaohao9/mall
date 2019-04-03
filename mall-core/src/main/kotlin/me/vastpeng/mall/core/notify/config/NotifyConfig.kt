package me.vastpeng.mall.core.notify.config

import me.vastpeng.mall.core.notify.NotifyService
import me.vastpeng.mall.core.notify.sender.WxTemplateSender
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.JavaMailSender


@Configuration
@EnableConfigurationProperties(NotifyProperties::class)
class NotifyAutoConfiguration(private val properties: NotifyProperties) {

    @Bean
    fun notifyService(): NotifyService {
        val notifyService = NotifyService()

        val mailConfig = properties.mail
        if (mailConfig.isEnable) {
            notifyService.setMailSender(mailSender())
            notifyService.setSendFrom(mailConfig.sendfrom)
            notifyService.setSendTo(mailConfig.sendto)
        }


        val wxConfig = properties.wx
        if (wxConfig.isEnable) {
            notifyService.setWxTemplateSender(wxTemplateSender())
            notifyService.setWxTemplate(wxConfig.template)
        }
        return notifyService
    }

    @Bean
    fun mailSender(): JavaMailSender {
        val mailConfig = properties.mail
        val mailSender = JavaMailSenderImpl()
        mailSender.host = mailConfig.host
        mailSender.username = mailConfig.username
        mailSender.password = mailConfig.password
        return mailSender
    }

    @Bean
    fun wxTemplateSender(): WxTemplateSender {
        return WxTemplateSender()
    }
}

