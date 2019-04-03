package me.vastpeng.mall.core.util

import java.util.*

class CharUtil {
    companion object {

        fun getRandomString(num: Int): String {
            val base = "abcdefghijklmnopqrstuvwxyz0123456789"
            val random = Random()
            val sb = StringBuffer()
            for (i in 0 until num) {
                val number = random.nextInt(base.length)
                sb.append(base[number])
            }
            return sb.toString()
        }

        fun getRandomNum(num: Int): String {
            val base = "0123456789"
            val random = Random()
            val sb = StringBuffer()
            for (i in 0 until num) {
                val number = random.nextInt(base.length)
                sb.append(base[number])
            }
            return sb.toString()
        }
    }
}