package me.vastpeng.mall.core.util

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime


/**
 * 日期格式化工具类
 */
class DateTimeUtil {
    companion object {
        /**
         * 格式 yyyy年MM月dd日 HH:mm:ss
         *
         * @param dateTime
         * @return
         */
        fun getDateTimeDisplayString(dateTime: LocalDateTime): String {
            val dtf2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")
            return dtf2.format(dateTime)
        }
    }
}