package me.vastpeng.mall.core.config

import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import com.github.binarywang.wxpay.service.WxPayService
import com.github.binarywang.wxpay.config.WxPayConfig
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.config.WxMaConfig
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig
import org.springframework.context.annotation.Bean


@Configuration
class WeixinConfig {
    @Autowired
    private lateinit var properties: WxProperties

    @Bean
    fun wxMaConfig(): WxMaConfig {
        val config = WxMaInMemoryConfig()
        config.appid = properties.appId
        config.secret = properties.appSecret
        return config
    }


    @Bean
    fun wxMaService(maConfig: WxMaConfig): WxMaService {
        val service = WxMaServiceImpl()
        service.wxMaConfig = maConfig
        return service
    }

    @Bean
    fun wxPayConfig(): WxPayConfig {
        val payConfig = WxPayConfig()
        payConfig.appId = properties.appId
        payConfig.mchId = properties.mchId
        payConfig.mchKey = properties.mchKey
        payConfig.notifyUrl = properties.notifyUrl
        payConfig.keyPath = properties.keyPath
        payConfig.tradeType = "JSAPI"
        payConfig.signType = "MD5"
        return payConfig
    }


    @Bean
    fun wxPayService(payConfig: WxPayConfig): WxPayService {
        val wxPayService = WxPayServiceImpl()
        wxPayService.config = payConfig
        return wxPayService
    }
}