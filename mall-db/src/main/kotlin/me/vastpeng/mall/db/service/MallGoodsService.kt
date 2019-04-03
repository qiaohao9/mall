package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallGoodsMapper
import me.vastpeng.mall.db.domain.MallGoods
import me.vastpeng.mall.db.domain.MallGoodsExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import javax.annotation.Resource

@Service
class MallGoodsService {
    var columns = arrayOf(MallGoods.Column.id, MallGoods.Column.name, MallGoods.Column.brief, MallGoods.Column.picUrl, MallGoods.Column.isHot, MallGoods.Column.isNew, MallGoods.Column.counterPrice, MallGoods.Column.retailPrice)
    @Resource
    private var goodsMapper: MallGoodsMapper? = null

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
}
