package me.vastpeng.mall.core.notify

enum class NotifyType private constructor(val type: String) {
    PAY_SUCCEED("paySucceed"),
    SHIP("ship"),
    REFUND("refund"),
    CAPTCHA("captcha")
}