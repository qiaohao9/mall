package me.vastpeng.mall.db.dao

import me.vastpeng.mall.db.domain.MallOrder
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

interface OrderMapper {
    fun updateWithOptimisticLocker(@Param("lastUpdateTime") lastUpdateTime: LocalDateTime, @Param("order") order: MallOrder): Int
}