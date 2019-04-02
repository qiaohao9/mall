package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.MallGoodsAttributeMapper
import me.vastpeng.mall.db.domain.MallGoodsAttribute
import me.vastpeng.mall.db.domain.MallGoodsAttributeExample
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallGoodsAttributeService {
    @Resource
    private var goodsAttributeMapper: MallGoodsAttributeMapper? = null

    fun queryByGid(goodsId: Int): List<MallGoodsAttribute> {
        var example: MallGoodsAttributeExample = MallGoodsAttributeExample()
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false)
        return goodsAttributeMapper!!.selectByExample(example)
    }

    fun add(goodsAttribute: MallGoodsAttribute) {
        goodsAttribute.addTime = LocalDateTime.now()
        goodsAttribute.updateTime = LocalDateTime.now()
        goodsAttributeMapper!!.insertSelective(goodsAttribute)
    }

    fun findById(id: Int): MallGoodsAttribute {
        return goodsAttributeMapper!!.selectByPrimaryKey(id)
    }

    fun deleteByGid(gid: Int) {
        var example: MallGoodsAttributeExample = MallGoodsAttributeExample()
        example.or().andGoodsIdEqualTo(gid)
        goodsAttributeMapper!!.logicalDeleteByExample(example)
    }

}