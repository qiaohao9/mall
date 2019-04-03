package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallGoodsMapper
import me.vastpeng.mall.db.domain.MallGoods
import me.vastpeng.mall.db.domain.MallGoodsExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import javax.annotation.Resource
import java.time.LocalDateTime
import java.util.ArrayList


@Service
class MallGoodsService {
    var columns = arrayOf(MallGoods.Column.id, MallGoods.Column.name, MallGoods.Column.brief, MallGoods.Column.picUrl, MallGoods.Column.isHot, MallGoods.Column.isNew, MallGoods.Column.counterPrice, MallGoods.Column.retailPrice)
    @Resource
    private lateinit var goodsMapper: MallGoodsMapper

    fun queryByHot(offset: Int, limit: Int): List<MallGoods> {
        var example: MallGoodsExample = MallGoodsExample()
        example.or().andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false)
        example.orderByClause = "add_time desc"
        PageHelper.startPage<Int>(offset, limit)
        return goodsMapper!!.selectByExampleSelective(example, *columns)
    }

    fun queryByNew(offset: Int, limit: Int): List<MallGoods> {
        var example: MallGoodsExample = MallGoodsExample()
        example.or().andIsNewEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false)
        example.orderByClause = "add_time desc"
        PageHelper.startPage<Int>(offset, limit)
        return goodsMapper!!.selectByExampleSelective(example, *columns)
    }

    fun queryByCategory(catList: List<Int>, offset: Int, limit: Int): List<MallGoods> {
        var example: MallGoodsExample = MallGoodsExample()
        example.or().andCategoryIdIn(catList).andIsOnSaleEqualTo(true).andDeletedEqualTo(false)
        example.orderByClause = "add_time desc"
        PageHelper.startPage<Int>(offset, limit)
        return goodsMapper!!.selectByExampleSelective(example, *columns)
    }

    fun queryByCategory(catId: Int, offset: Int, limit: Int): List<MallGoods> {
        var example: MallGoodsExample = MallGoodsExample()
        example.or().andCategoryIdEqualTo(catId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false)
        example.orderByClause = "add_time desc"
        PageHelper.startPage<Int>(offset, limit)
        return goodsMapper!!.selectByExampleSelective(example, *columns)
    }

    fun querySelective(catId: Int, brandId: Int, keywords: String, isHot: Boolean, isNew: Boolean, offset: Int, limit: Int, sort: String, order: String): List<MallGoods> {
        var example: MallGoodsExample = MallGoodsExample()
        var criteria1: MallGoodsExample.Criteria = example.or()
        var criteria2: MallGoodsExample.Criteria = example.or()

        if (!StringUtils.isEmpty(catId) && catId != 0) {
            criteria1.andCategoryIdEqualTo(catId)
            criteria2.andCategoryIdEqualTo(catId)
        }
        if (!StringUtils.isEmpty(brandId)) {
            criteria1.andBrandIdEqualTo(brandId)
            criteria2.andBrandIdEqualTo(brandId)
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew)
            criteria2.andIsNewEqualTo(isNew)
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot)
            criteria2.andIsHotEqualTo(isHot)
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%$keywords%")
            criteria2.andNameLike("%$keywords%")
        }
        criteria1.andIsOnSaleEqualTo(true)
        criteria2.andIsOnSaleEqualTo(true)
        criteria1.andDeletedEqualTo(false)
        criteria2.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) example.orderByClause = "$sort $order"

        PageHelper.startPage<Int>(offset, limit)

        return goodsMapper!!.selectByExampleSelective(example, *columns)
    }

    fun querySelective(goodsSn: String, name: String, page: Int?, size: Int?, sort: String, order: String): List<MallGoods> {
        val example = MallGoodsExample()
        val criteria = example.createCriteria()

        if (!StringUtils.isEmpty(goodsSn)) {
            criteria.andGoodsSnEqualTo(goodsSn)
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%$name%")
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Any>(page!!, size!!)
        return goodsMapper.selectByExampleWithBLOBs(example)
    }

    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    fun findById(id: Int?): MallGoods {
        val example = MallGoodsExample()
        example.or().andIdEqualTo(id).andDeletedEqualTo(false)
        return goodsMapper.selectOneByExampleWithBLOBs(example)
    }

    /**
     * 获取某个商品信息，仅展示相关内容
     *
     * @param id
     * @return
     */
    fun findByIdVO(id: Int?): MallGoods {
        val example = MallGoodsExample()
        example.or().andIdEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false)
        return goodsMapper.selectOneByExampleSelective(example, *columns)
    }


    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    fun queryOnSale(): Int? {
        val example = MallGoodsExample()
        example.or().andIsOnSaleEqualTo(true).andDeletedEqualTo(false)
        return goodsMapper.countByExample(example) as Int
    }

    fun updateById(goods: MallGoods): Int {
        goods.updateTime = LocalDateTime.now()
        return goodsMapper.updateByPrimaryKeySelective(goods)
    }

    fun deleteById(id: Int?) {
        goodsMapper.logicalDeleteByPrimaryKey(id)
    }

    fun add(goods: MallGoods) {
        goods.addTime = LocalDateTime.now()
        goods.updateTime = LocalDateTime.now()
        goodsMapper.insertSelective(goods)
    }

    /**
     * 获取所有物品总数，包括在售的和下架的，但是不包括已删除的商品
     *
     * @return
     */
    fun count(): Int {
        val example = MallGoodsExample()
        example.or().andDeletedEqualTo(false)
        return goodsMapper.countByExample(example) as Int
    }

    fun getCatIds(brandId: Int?, keywords: String, isHot: Boolean?, isNew: Boolean?): List<Int> {
        val example = MallGoodsExample()
        val criteria1 = example.or()
        val criteria2 = example.or()

        if (!StringUtils.isEmpty(brandId)) {
            criteria1.andBrandIdEqualTo(brandId)
            criteria2.andBrandIdEqualTo(brandId)
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew)
            criteria2.andIsNewEqualTo(isNew)
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot)
            criteria2.andIsHotEqualTo(isHot)
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%$keywords%")
            criteria2.andNameLike("%$keywords%")
        }
        criteria1.andIsOnSaleEqualTo(true)
        criteria2.andIsOnSaleEqualTo(true)
        criteria1.andDeletedEqualTo(false)
        criteria2.andDeletedEqualTo(false)

        val goodsList = goodsMapper.selectByExampleSelective(example, Column.categoryId)
        val cats = ArrayList<Int>()
        for (goods in goodsList) {
            cats.add(goods.categoryId)
        }
        return cats
    }

    fun checkExistByName(name: String): Boolean {
        val example = MallGoodsExample()
        example.or().andNameEqualTo(name).andIsOnSaleEqualTo(true).andDeletedEqualTo(false)
        return goodsMapper.countByExample(example) !== 0L
    }

}
