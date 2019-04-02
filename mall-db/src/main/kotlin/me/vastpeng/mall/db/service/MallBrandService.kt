package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallBrandMapper
import me.vastpeng.mall.db.domain.MallBrand
import me.vastpeng.mall.db.domain.MallBrandExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallBrandService {
    @Resource
    private lateinit var brandMapper: MallBrandMapper

    private var columns: Array<MallBrand.Column> = arrayOf(MallBrand.Column.id, MallBrand.Column.name, MallBrand.Column.desc, MallBrand.Column.picUrl, MallBrand.Column.floorPrice)

    fun queryVO(offset: Int, limit: Int): List<MallBrand> {
        var example: MallBrandExample = MallBrandExample()
        example.or().andDeletedEqualTo(false)
        example.orderByClause = "and_time desc"
        PageHelper.startPage<Int>(offset, limit)
        return brandMapper.selectByExampleSelective(example, *columns)
    }

    fun queryTotalCount(): Int {
        var example: MallBrandExample = MallBrandExample()
        example.or().andDeletedEqualTo(false)
        return brandMapper.countByExample(example).toInt()
    }

    fun findById(id: Int): MallBrand {
        return brandMapper.selectByPrimaryKey(id)
    }

    fun querySelective(id: String, name: String, page: Int, size: Int, sort: String, order: String): List<MallBrand> {
        var example: MallBrandExample = MallBrandExample()
        var criteria: MallBrandExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id))
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%$name%")
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, size)
        return brandMapper.selectByExample(example)
    }

    fun updateById(brand: MallBrand): Int {
        brand.updateTime = LocalDateTime.now()
        return brandMapper.updateByPrimaryKeySelective(brand)
    }

    fun deleteById(id: Int) {
        brandMapper.logicalDeleteByPrimaryKey(id)
    }

    fun add(brand: MallBrand) {
        brand.addTime = LocalDateTime.now()
        brand.updateTime = LocalDateTime.now()
        brandMapper.insertSelective(brand)
    }

    fun all(): List<MallBrand> {
        var example: MallBrandExample = MallBrandExample()
        example.or().andDeletedEqualTo(false)
        return brandMapper.selectByExample(example)
    }

}