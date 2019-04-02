package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallCategoryMapper
import me.vastpeng.mall.db.domain.MallCategory
import me.vastpeng.mall.db.domain.MallCategoryExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallCategoryService {
    @Resource
    private var categoryMapper: MallCategoryMapper? = null

    private var CHANNEL: Array<MallCategory.Column> = arrayOf(MallCategory.Column.id, MallCategory.Column.name, MallCategory.Column.iconUrl)

    fun queryL1WithoutRecommend(offset: Int, limit: Int): List<MallCategory> {
        var example: MallCategoryExample = MallCategoryExample()
        example.or().andLevelEqualTo("L1").andNameEqualTo("推荐").andDeletedEqualTo(false)
        PageHelper.startPage<Int>(offset, limit)
        return categoryMapper!!.selectByExample(example)
    }

    fun queryL1(offset: Int, limit: Int): List<MallCategory> {
        var example: MallCategoryExample = MallCategoryExample()
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false)
        PageHelper.startPage<Int>(offset, limit)
        return categoryMapper!!.selectByExample(example)
    }

    fun queryL1(): List<MallCategory> {
        var example: MallCategoryExample = MallCategoryExample()
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false)
        return categoryMapper!!.selectByExample(example)
    }

    fun queryByPid(pid: Int): List<MallCategory> {
        var example: MallCategoryExample = MallCategoryExample()
        example.or().andPidEqualTo(pid).andDeletedEqualTo(false)
        return categoryMapper!!.selectByExample(example)
    }

    fun queryL2ByIds(ids: List<Int>): List<MallCategory> {
        var example: MallCategoryExample = MallCategoryExample()
        example.or().andIdIn(ids).andLevelEqualTo("L2").andDeletedEqualTo(false)
        return categoryMapper!!.selectByExample(example)
    }

    fun findById(id: Int): MallCategory {
        return categoryMapper!!.selectByPrimaryKey(id)
    }

    fun querySelective(id: String, name: String, page: Int, size: Int, sort: String, order: String): List<MallCategory> {
        var example: MallCategoryExample = MallCategoryExample()
        var criteria: MallCategoryExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id))
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameEqualTo("%$name%")
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, size)
        return categoryMapper!!.selectByExample(example)
    }

    fun updateById(category: MallCategory): Int {
        category.updateTime = LocalDateTime.now()
        return categoryMapper!!.updateByPrimaryKeySelective(category)
    }

    fun deleteById(id: Int) {
        categoryMapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun add(category: MallCategory) {
        category.addTime = LocalDateTime.now()
        category.updateTime = LocalDateTime.now()
        categoryMapper!!.insertSelective(category)
    }

    fun queryChannel(): List<MallCategory> {
        var example: MallCategoryExample = MallCategoryExample()
        example.or().andLevelEqualTo("L1").andDeletedEqualTo(false)
        return categoryMapper!!.selectByExampleSelective(example, *CHANNEL)
    }
}

