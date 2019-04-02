package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.MallOrderGoodsMapper
import me.vastpeng.mall.db.domain.MallOrderGoods
import me.vastpeng.mall.db.domain.MallOrderGoodsExample
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallOrderGoodsService {
    @Resource
    private var orderGoodsMapper: MallOrderGoodsMapper? = null

    fun add(orderGoods: MallOrderGoods): Int {
        orderGoods.addTime = LocalDateTime.now()
        orderGoods.updateTime = LocalDateTime.now()
        return orderGoodsMapper!!.insertSelective(orderGoods)
    }

    fun queryByOid(orderId: Int): List<MallOrderGoods> {
        var example = MallOrderGoodsExample()
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false)
        return orderGoodsMapper!!.selectByExample(example)
    }

    fun findByOidAndGid(orderId: Int, goodsId: Int): List<MallOrderGoods> {
        var example = MallOrderGoodsExample()
        example.or().andOrderIdEqualTo(orderId).andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false)
        return orderGoodsMapper!!.selectByExample(example)
    }

    fun findById(id: Int): MallOrderGoods {
        return orderGoodsMapper!!.selectByPrimaryKey(id)
    }

    fun updateById(orderGoods: MallOrderGoods) {
        orderGoods.updateTime = LocalDateTime.now()
        orderGoodsMapper!!.updateByPrimaryKeySelective(orderGoods)
    }

    fun getComments(orderId: Int): Short {
        var example = MallOrderGoodsExample()
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false)
        val count = orderGoodsMapper!!.countByExample(example)
        return count.toShort()
    }

    fun checkExist(goodsId: Int): Boolean {
        var example = MallOrderGoodsExample()
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false)
        return orderGoodsMapper!!.countByExample(example) != 0L
    }
}