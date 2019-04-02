package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.StatMapper
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class StatService {
    @Resource
    private val statMapper: StatMapper? = null


    fun statUser(): List<Map<*, *>> {
        return statMapper!!.statUser()
    }

    fun statOrder(): List<Map<*, *>> {
        return statMapper!!.statOrder()
    }

    fun statGoods(): List<Map<*, *>> {
        return statMapper!!.statGoods()
    }
}