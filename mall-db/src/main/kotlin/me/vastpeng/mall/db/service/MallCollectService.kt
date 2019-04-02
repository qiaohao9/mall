package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallCollectMapper
import me.vastpeng.mall.db.domain.MallCollect
import me.vastpeng.mall.db.domain.MallCollectExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallCollectService {
    @Resource
    private lateinit var collectMapper: MallCollectMapper

    fun count(uid: Int, gid: Int): Int {
        var example: MallCollectExample = MallCollectExample()
        example.or().andUserIdEqualTo(uid).andValueIdEqualTo(gid).andDeletedEqualTo(false)
        return collectMapper.countByExample(example).toInt()
    }

    fun queryByType(userId: Int, type: Byte, page: Int, size: Int): List<MallCollect> {
        var example: MallCollectExample = MallCollectExample()
        example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andDeletedEqualTo(false)
        example.orderByClause = MallCollect.Column.addTime.desc()
        PageHelper.startPage<Int>(page, size)
        return collectMapper.selectByExample(example)
    }

    fun countByType(userId: Int, type: Byte): Int {
        var example: MallCollectExample = MallCollectExample()
        example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andDeletedEqualTo(false)
        return collectMapper.countByExample(example).toInt()
    }

    fun queryByTypeAndValue(userId: Int, type: Byte, valueId: Int): List<MallCollect> {
        var example: MallCollectExample = MallCollectExample()
        example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andValueIdEqualTo(valueId).andDeletedEqualTo(false)
        return collectMapper.selectByExample(example)
    }

    fun deleteById(id: Int) {
        collectMapper.logicalDeleteByPrimaryKey(id)
    }

    fun add(collect: MallCollect): Int {
        collect.addTime = LocalDateTime.now()
        collect.updateTime = LocalDateTime.now()
        return collectMapper.insertSelective(collect)
    }

    fun querySelective(userId: String, valueId: String, page: Int, size: Int, sort: String, order: String): List<MallCollect> {
        var example: MallCollectExample = MallCollectExample()
        var criteria: MallCollectExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId))
        }
        if (!StringUtils.isEmpty(valueId)) {
            criteria.andValueIdEqualTo(Integer.valueOf(valueId))
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, size)
        return collectMapper.selectByExample(example)
    }
}