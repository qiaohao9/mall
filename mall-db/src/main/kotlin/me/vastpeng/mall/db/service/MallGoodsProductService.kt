package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.GoodsProductMapper
import me.vastpeng.mall.db.dao.MallGoodsProductMapper
import me.vastpeng.mall.db.domain.MallGoodsProduct
import me.vastpeng.mall.db.domain.MallGoodsProductExample
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallGoodsProductService {
    @Resource
    private lateinit var mallGoodsProductMapper: MallGoodsProductMapper
    @Resource
    private lateinit var goodsProductMapper: GoodsProductMapper

    fun queryByGid(gid: Int): List<MallGoodsProduct> {
        var example: MallGoodsProductExample = MallGoodsProductExample()
        example.or().andGoodsIdEqualTo(gid).andDeletedEqualTo(false)
        return mallGoodsProductMapper.selectByExample(example)
    }

    fun findById(id: Int): MallGoodsProduct {
        return mallGoodsProductMapper.selectByPrimaryKey(id)
    }

    fun deleteById(id: Int) {
        mallGoodsProductMapper.logicalDeleteByPrimaryKey(id)
    }

    fun add(goodsProduct: MallGoodsProduct) {
        goodsProduct.addTime = LocalDateTime.now()
        goodsProduct.updateTime = LocalDateTime.now()
        mallGoodsProductMapper.insertSelective(goodsProduct)
    }

    fun count(): Int {
        var example: MallGoodsProductExample = MallGoodsProductExample()
        example.or().andDeletedEqualTo(false)
        return mallGoodsProductMapper.countByExample(example).toInt()
    }

    fun deleteByGid(gid: Int) {
        var example: MallGoodsProductExample = MallGoodsProductExample()
        example.or().andGoodsIdEqualTo(gid)
        mallGoodsProductMapper.logicalDeleteByExample(example)
    }

    fun addStock(id: Int, num: Int): Int {
        return goodsProductMapper.addStock(id, num)
    }

    fun reduceStock(id: Int, num: Int): Int {
        return goodsProductMapper.reduceStock(id, num)
    }
}