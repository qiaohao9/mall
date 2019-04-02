package me.vastpeng.mall.db.dao

interface StatMapper {
    fun statUser(): List<Map<*, *>>
    fun statOrder(): List<Map<*, *>>
    fun statGoods(): List<Map<*, *>>
}