package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallGoodsMapper
import me.vastpeng.mall.db.dao.MallGrouponRulesMapper
import me.vastpeng.mall.db.domain.MallGoods
import me.vastpeng.mall.db.domain.MallGrouponRules
import me.vastpeng.mall.db.domain.MallGrouponRulesExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallGrouponRulesService {
    @Resource
    private var mapper: MallGrouponRulesMapper? = null
    @Resource
    private var goodsMapper: MallGoodsMapper? = null
    private var goodsColumns = arrayOf(MallGoods.Column.id, MallGoods.Column.name, MallGoods.Column.brief, MallGoods.Column.picUrl, MallGoods.Column.counterPrice, MallGoods.Column.retailPrice)

    fun createRules(rules: MallGrouponRules): Int {
        rules.addTime = LocalDateTime.now()
        rules.updateTime = LocalDateTime.now()
        return mapper!!.insertSelective(rules)
    }

    /**
     * 根据ID查找对应团购项
     */
    fun queryById(id: Int): MallGrouponRules? {
        var example = MallGrouponRulesExample()
        example.or().andIdEqualTo(id).andDeletedEqualTo(false)
        return mapper!!.selectOneByExample(example)
    }

    /**
     * 查询某个商品关联的团购规则
     */
    fun queryByGoodsId(goodsId: Int): List<MallGrouponRules> {
        var example = MallGrouponRulesExample()
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false)
        return mapper!!.selectByExample(example)
    }

    /**
     * 获取首页团购活动列表
     */
    fun queryList(offset: Int, limit: Int, sort: String, order: String): List<Map<String, Any>> {
        var example = MallGrouponRulesExample()
        example.or().andDeletedEqualTo(false)
        example.orderByClause = "$sort $order"
        PageHelper.startPage<Int>(offset, limit)
        var grouponRules = mapper!!.selectByExample(example)

        var grouponList = ArrayList<Map<String, Any>>(grouponRules.size)
        for (rule in grouponRules) {
            var goodsId = rule.goodsId
            var goods = goodsMapper!!.selectByPrimaryKeySelective(goodsId, *goodsColumns) ?: continue

            var item = HashMap<String, Any>()
            item["goods"] = goods
            item["groupon_price"] = goods!!.retailPrice.subtract(rule.discount)
            item["groupon_member"] = rule.discount
            grouponList.add(item)
        }

        return grouponList
    }

    /**
     * 判断某个团购活动是否已经过期
     */
    fun isExpired(rules: MallGrouponRules): Boolean = (rules == null || rules.expireTime.isBefore(LocalDateTime.now()))

    fun querySelective(goodsId: String, page: Int, size: Int, sort: String, order: String): List<MallGrouponRules> {
        var example = MallGrouponRulesExample()
        example.orderByClause = "$sort $order"

        var criteria = example.createCriteria()

        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.parseInt(goodsId))
        }
        criteria.andDeletedEqualTo(false)

        PageHelper.startPage<Int>(page, size)
        return mapper!!.selectByExample(example)
    }

    fun delete(id: Int) {
        mapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun updateById(grouponRules: MallGrouponRules): Int {
        grouponRules.updateTime = LocalDateTime.now()
        return mapper!!.updateByPrimaryKeySelective(grouponRules)
    }
}