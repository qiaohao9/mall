package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallCartMapper
import me.vastpeng.mall.db.domain.MallCart
import me.vastpeng.mall.db.domain.MallCartExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallCartService {
    @Resource
    private var cartMapper: MallCartMapper? = null

    fun queryExist(goodsId: Int, productId: Int, userId: Int): List<MallCart> {
        var example: MallCartExample = MallCartExample()
        example.or().andGoodsIdEqualTo(goodsId).andProductIdEqualTo(productId).andUserIdEqualTo(userId).andDeletedEqualTo(false)
        return cartMapper!!.selectByExample(example)
    }

    fun add(cart: MallCart) {
        cart.addTime = LocalDateTime.now()
        cart.updateTime = LocalDateTime.now()
        cartMapper!!.insertSelective(cart)
    }

    fun updateById(cart: MallCart): Int {
        cart.updateTime = LocalDateTime.now()
        return cartMapper!!.updateByPrimaryKeySelective(cart)
    }

    fun queryByUid(userId: Int): List<MallCart> {
        var example: MallCartExample = MallCartExample()
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false)
        return cartMapper!!.selectByExample(example)
    }

    fun queryByUidAndChecked(userId: Int): List<MallCart> {
        var example: MallCartExample = MallCartExample()
        example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true).andDeletedEqualTo(false)
        return cartMapper!!.selectByExample(example)
    }

    fun delete(productList: List<Int>, userId: Int): Int {
        var example: MallCartExample = MallCartExample()
        example.or().andUserIdEqualTo(userId).andProductIdIn(productList)
        return cartMapper!!.logicalDeleteByExample(example)
    }

    fun findById(id: Int): MallCart {
        return cartMapper!!.selectByPrimaryKey(id)
    }

    fun updateCheck(userId: Int, idsList: List<Int>, checked: Boolean): Int {
        var example: MallCartExample = MallCartExample()
        example.or().andUserIdEqualTo(userId).andProductIdIn(idsList).andDeletedEqualTo(false)

        var cart: MallCart = MallCart()
        cart.checked = checked
        cart.updateTime = LocalDateTime.now()

        return cartMapper!!.updateByExampleSelective(cart, example)
    }

    fun clearGoods(userId: Int) {
        var example: MallCartExample = MallCartExample()
        example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true)

        var cart: MallCart = MallCart()
        cart.deleted = true
        cartMapper!!.updateByExampleSelective(cart, example)
    }

    fun querySelective(userId: Int, goodsId: Int, page: Int, limit: Int, sort: String, order: String): List<MallCart> {
        var example: MallCartExample = MallCartExample()
        var criteria: MallCartExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(userId)
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(goodsId)
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, limit)
        return cartMapper!!.selectByExample(example)
    }

    fun deleteById(id: Int) {
        cartMapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun checkExist(goodsId: Int): Boolean {
        var example: MallCartExample = MallCartExample()
        example.or().andGoodsIdEqualTo(goodsId).andCheckedEqualTo(true).andDeletedEqualTo(false)
        return cartMapper!!.countByExample(example) != 0L
    }
}