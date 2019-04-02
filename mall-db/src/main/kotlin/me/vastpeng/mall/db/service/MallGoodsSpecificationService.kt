package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.MallGoodsSpecificationMapper
import me.vastpeng.mall.db.domain.MallGoodsSpecification
import me.vastpeng.mall.db.domain.MallGoodsSpecificationExample
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallGoodsSpecificationService {
    @Resource
    private lateinit var goodsSpecificationMapper: MallGoodsSpecificationMapper

    fun queryByGid(gid: Int): List<MallGoodsSpecification> {
        var example: MallGoodsSpecificationExample = MallGoodsSpecificationExample()
        example.or().andGoodsIdEqualTo(gid).andDeletedEqualTo(false)
        return goodsSpecificationMapper.selectByExample(example)
    }

    fun findById(id: Int): MallGoodsSpecification {
        return goodsSpecificationMapper.selectByPrimaryKey(id)
    }

    fun deleteByGid(gid: Int) {
        var example: MallGoodsSpecificationExample = MallGoodsSpecificationExample()
        example.or().andGoodsIdEqualTo(gid)
        goodsSpecificationMapper.logicalDeleteByExample(example)
    }

    fun add(goodsSpecification: MallGoodsSpecification) {
        goodsSpecification.addTime = LocalDateTime.now()
        goodsSpecification.updateTime = LocalDateTime.now()
        goodsSpecificationMapper.insertSelective(goodsSpecification)
    }

    /**
     * * [
     * {
     * name: '',
     * valueList: [ {}, {}]
     * },
     * {
     * name: '',
     * valueList: [ {}, {}]
     * }
     * ]
     */
    fun getSpecificationVoList(id: Int): List<VO> {
        var goodsSpecificationList: List<MallGoodsSpecification> = queryByGid(gid = id)

        var map = HashMap<String, VO>()
        var specificationVoList = ArrayList<VO>()

        for (goodsSpecification: MallGoodsSpecification in goodsSpecificationList) {
            var specification: String = goodsSpecification.specification
            var goodsSpecificationVo: VO? = map[specification]
            if (goodsSpecificationVo == null) {
                var goodsSpecificationVo = VO()
                goodsSpecificationVo.name = specification
                var valueList = ArrayList<MallGoodsSpecification>()
                valueList.add(goodsSpecification)
                goodsSpecificationVo.valueList = valueList
                map[specification] = goodsSpecificationVo
                specificationVoList.add(goodsSpecificationVo)
            } else {
                var valueList = goodsSpecificationVo.valueList
                valueList!!.add(goodsSpecification)
            }
        }

        return specificationVoList
    }

    data class VO(
            var name: String = "",
            var valueList: ArrayList<MallGoodsSpecification>? = null
    )
}

