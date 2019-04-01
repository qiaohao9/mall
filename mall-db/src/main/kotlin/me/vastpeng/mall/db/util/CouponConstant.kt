package me.vastpeng.mall.db.util

class CouponConstant {
    companion object {
        const val TYPE_COMMON: Short = 0
        const val TYPE_REGISTER: Short = 1
        const val TYPE_CODE: Short = 2

        const val GOODS_TYPE_ALL: Short = 0
        const val GOODS_TYPE_CATEGORY: Short = 1
        const val GOODS_TYPE_ARRAY: Short = 2

        const val STATUS_NORMAL: Short = 0
        const val STATUS_EXPIRED: Short = 1
        const val STATUS_OUT: Short = 2

        const val TIME_TYPE_DAYS: Short = 0
        const val TIME_TYPE_TIME: Short = 1
    }
}