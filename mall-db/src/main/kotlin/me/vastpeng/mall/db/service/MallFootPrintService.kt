package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallFootprintMapper
import me.vastpeng.mall.db.domain.MallFootprint
import me.vastpeng.mall.db.domain.MallFootprintExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallFootPrintService {
    @Resource
    private lateinit var footprintMapper: MallFootprintMapper

    fun queryByAddTime(userId: Int, page: Int, size: Int): List<MallFootprint> {
        var example: MallFootprintExample = MallFootprintExample()
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        example.orderByClause = MallFootprint.Column.addTime.desc()
        PageHelper.startPage<Int>(page, size)
        return footprintMapper.selectByExample(example)
    }

    fun findById(id: Int): MallFootprint {
        return footprintMapper.selectByPrimaryKey(id);
    }

    fun deleteById(id: Int) {
        footprintMapper.logicalDeleteByPrimaryKey(id);
    }

    fun add(footprint: MallFootprint) {
        footprint.addTime = LocalDateTime.now()
        footprint.updateTime = LocalDateTime.now()
        footprintMapper.insertSelective(footprint);
    }

    fun querySelective(userId: String, goodsId: String, page: Int, size: Int, sort: String, order: String): List<MallFootprint> {
        var example: MallFootprintExample = MallFootprintExample()
        var criteria: MallFootprintExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage<Int>(page, size)
        return footprintMapper.selectByExample(example);
    }
}