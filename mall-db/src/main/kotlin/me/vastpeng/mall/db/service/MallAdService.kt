package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallAdMapper
import me.vastpeng.mall.db.domain.MallAd
import me.vastpeng.mall.db.domain.MallAdExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallAdService {
    @Resource
    private lateinit var adMapper: MallAdMapper

    fun queryIndex(): List<MallAd> {
        var example: MallAdExample = MallAdExample()
        example.or().andPositionEqualTo(1).andDeletedEqualTo(false).andEnabledEqualTo(true)
        return adMapper.selectByExample(example)
    }

    fun querySelective(name: String, content: String, page: Int, limit: Int, sort: String, order: String): List<MallAd> {
        var example: MallAdExample = MallAdExample()
        var critera: MallAdExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(name)) {
            critera.andNameEqualTo("%$name%")
        }
        if (!StringUtils.isEmpty(content)) {
            critera.andContentEqualTo("%$content%")
        }

        critera.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, limit)
        return adMapper.selectByExample(example)
    }

    fun updateById(ad: MallAd): Int {
        ad.updateTime = LocalDateTime.now()
        return adMapper.updateByPrimaryKeySelective(ad)
    }

    fun deleteById(id: Int) {
        adMapper.logicalDeleteByPrimaryKey(id)
    }

    fun add(ad: MallAd) {
        ad.addTime = LocalDateTime.now()
        ad.updateTime = LocalDateTime.now()
        adMapper.insertSelective(ad)
    }

    fun findById(id: Int): MallAd {
        return adMapper.selectByPrimaryKey(id)
    }
}