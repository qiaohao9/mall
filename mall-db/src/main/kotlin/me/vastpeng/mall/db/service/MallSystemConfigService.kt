package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.MallSystemMapper
import me.vastpeng.mall.db.domain.MallSystem
import me.vastpeng.mall.db.domain.MallSystemExample
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class MallSystemConfigService {
    @Resource
    private var systemMapper: MallSystemMapper? = null

    fun queryAll(): List<MallSystem> {
        var example = MallSystemExample()
        example.or()
        return systemMapper!!.selectByExample(example)
    }
}