package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallGrouponMapper
import me.vastpeng.mall.db.domain.MallGroupon
import me.vastpeng.mall.db.domain.MallGrouponExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallGrouponService {
    @Resource
    private var mapper: MallGrouponMapper? = null

    /**
     * 获取用户发起的团购记录
     */
    fun queryMyGroupon(userId: Int): List<MallGroupon> {
        var example = MallGrouponExample()
        example.or().andUserIdEqualTo(userId).andCreatorUserIdEqualTo(userId).andGrouponIdEqualTo(0).andDeletedEqualTo(false).andPayedEqualTo(true)
        example.orderBy("add_time desc")
        return mapper!!.selectByExample(example)
    }

    /**
     * 获取用户参与的团购记录
     */
    fun queryMyJoinGroupon(userId: Int): List<MallGroupon> {
        var example = MallGrouponExample()
        example.or().andUserIdEqualTo(userId).andGrouponIdNotEqualTo(0).andDeletedEqualTo(false).andPayedEqualTo(true)
        example.orderBy("add_time desc")
        return mapper!!.selectByExample(example)
    }

    /**
     * 根据OrderId查询团购记录
     */
    fun queryByOrderId(orderId: Int): MallGroupon {
        var example = MallGrouponExample()
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false)
        return mapper!!.selectOneByExample(example)
    }

    /**
     * 获取某个团购活动参与的记录
     */
    fun queryJoinRecord(id: Int): List<MallGroupon> {
        var example = MallGrouponExample()
        example.or().andGrouponIdEqualTo(id).andDeletedEqualTo(false).andPayedEqualTo(true)
        example.orderBy("add_time desc")
        return mapper!!.selectByExample(example)
    }

    fun queryById(id: Int): MallGroupon {
        var example = MallGrouponExample()
        example.or().andIdEqualTo(id).andDeletedEqualTo(false).andPayedEqualTo(true)
        return mapper!!.selectOneByExample(example)
    }

    /**
     * 返回某个发起的团购参与人数
     */
    fun countGroupon(grouponId: Int): Int {
        var example = MallGrouponExample()
        example.or().andGrouponIdEqualTo(grouponId).andDeletedEqualTo(false).andPayedEqualTo(true)
        return mapper!!.countByExample(example) as Int
    }

    fun updateById(groupon: MallGroupon): Int {
        groupon.updateTime = LocalDateTime.now()
        return mapper!!.updateByPrimaryKeySelective(groupon)
    }

    fun createGroupon(groupon: MallGroupon): Int {
        groupon.addTime = LocalDateTime.now()
        groupon.updateTime = LocalDateTime.now()
        return mapper!!.insertSelective(groupon)
    }

    fun querySelective(rulesId: String, page: Int, size: Int, sort: String, order: String): List<MallGroupon> {
        var example = MallGrouponExample()
        var criteria = example.createCriteria()

        if (!StringUtils.isEmpty(rulesId)) {
            criteria.andRulesIdEqualTo(Integer.parseInt(rulesId))
        }
        criteria.andDeletedEqualTo(false)
        criteria.andPayedEqualTo(true)
        criteria.andGrouponIdEqualTo(0)

        PageHelper.startPage<Int>(page, size)
        return mapper!!.selectByExample(example)
    }
}