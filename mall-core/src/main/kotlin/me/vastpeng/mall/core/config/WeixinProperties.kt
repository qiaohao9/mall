package me.vastpeng.mall.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "mall.wx")
class WxProperties {

    var appId: String? = null

    var appSecret: String? = null

    var mchId: String? = null

    var mchKey: String? = null

    var notifyUrl: String? = null

    var keyPath: String? = null
}
