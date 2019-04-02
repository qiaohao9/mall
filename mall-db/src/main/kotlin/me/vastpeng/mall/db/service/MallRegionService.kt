package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallRegionMapper
import me.vastpeng.mall.db.domain.MallRegion
import me.vastpeng.mall.db.domain.MallRegionExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import javax.annotation.Resource

@Service
class MallRegionService {
    @Resource
    private var regionMapper: MallRegionMapper? = null

    fun getAll(): List<MallRegion> {
        var example = MallRegionExample()
        example.or().andTypeNotEqualTo(4);
        return regionMapper!!.selectByExample(example);
    }

    fun queryByPid(parentId: Int): List<MallRegion> {
        var example = MallRegionExample()
        example.or().andPidEqualTo(parentId);
        return regionMapper!!.selectByExample(example);
    }

    fun findById(id: Int): MallRegion {
        return regionMapper!!.selectByPrimaryKey(id);
    }

    fun querySelective(name: String, code: Int, page: Int, size: Int, sort: String, order: String): List<MallRegion> {
        var example = MallRegionExample()
        var criteria = example.createCriteria()

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%$name%");
        }
        if (!StringUtils.isEmpty(code)) {
            criteria.andCodeEqualTo(code);
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order";
        }

        PageHelper.startPage<Int>(page, size);
        return regionMapper!!.selectByExample(example);
    }
}