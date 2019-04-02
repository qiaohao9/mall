package me.vastpeng.mall.db.dao

import org.apache.ibatis.annotations.Param

interface GoodsProductMapper {
    fun addStock(@Param("id") id: Int, @Param("num") num: Int): Int
    fun reduceStock(@Param("id") id: Int, @Param("num") num: Int): Int
}