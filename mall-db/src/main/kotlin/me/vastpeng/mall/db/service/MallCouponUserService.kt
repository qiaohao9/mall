package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallCouponUserMapper
import me.vastpeng.mall.db.domain.MallCouponUser
import me.vastpeng.mall.db.domain.MallCouponUserExample
import me.vastpeng.mall.db.util.CouponUserConstant
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallCouponUserService {
    @Resource
    private lateinit var couponUserMapper: MallCouponUserMapper

    fun countCoupon(couponId: Int): Int {
        var example: MallCouponUserExample = MallCouponUserExample()
        example.or().andCouponIdEqualTo(couponId).andDeletedEqualTo(false)
        return couponUserMapper.countByExample(example).toInt()
    }

    fun countUserAndCoupon(userId: Int, couponId: Int): Int {
        var example: MallCouponUserExample = MallCouponUserExample()
        example.or().andUserIdEqualTo(userId).andCouponIdEqualTo(couponId).andDeletedEqualTo(false)
        return couponUserMapper.countByExample(example).toInt()
    }

    fun add(couponUser: MallCouponUser) {
        couponUser.addTime = LocalDateTime.now()
        couponUser.updateTime = LocalDateTime.now()
        couponUserMapper.insertSelective(couponUser)
    }

    fun queryList(userId: Int?, couponId: Int?, status: Short?, page: Int?, size: Int?, sort: String?, order: String?): List<MallCouponUser> {
        var example: MallCouponUserExample = MallCouponUserExample()
        var certeria: MallCouponUserExample.Criteria = example.createCriteria()
        if (userId != null) {
            certeria.andUserIdEqualTo(userId)
        }
        if (couponId != null) {
            certeria.andCouponIdEqualTo(couponId)
        }
        if (status != null) {
            certeria.andStatusEqualTo(status)
        }

        certeria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }
        if (!StringUtils.isEmpty(page) && !StringUtils.isEmpty(size)) {
            PageHelper.startPage<Int>(page!!, size!!)
        }

        return couponUserMapper.selectByExample(example)
    }

    fun queryAll(userId: Int, couponId: Int): List<MallCouponUser> {
        return this.queryList(userId, couponId, CouponUserConstant.STATUS_USABLE, null, null, "add_time", "desc")
    }

    fun queryAll(userId: Int): List<MallCouponUser> {
        return this.queryList(userId, null, CouponUserConstant.STATUS_USABLE, null, null, "add_time", "desc")
    }

    fun queryOne(userId: Int, couponId: Int): MallCouponUser? {
        var couponUserList: List<MallCouponUser> = this.queryList(userId, couponId, CouponUserConstant.STATUS_USABLE, 1, 1, "add_time", "desc")
        if (couponUserList.isEmpty()) {
            return null
        }
        return couponUserList[0]
    }

    fun findById(id: Int): MallCouponUser {
        return couponUserMapper.selectByPrimaryKey(id)
    }

    fun update(couponUser: MallCouponUser): Int {
        couponUser.updateTime = LocalDateTime.now()
        return couponUserMapper.updateByPrimaryKeySelective(couponUser)
    }

    fun queryExpired(): List<MallCouponUser> {
        var example: MallCouponUserExample = MallCouponUserExample()
        example.or().andStatusEqualTo(CouponUserConstant.STATUS_USABLE).andEndTimeLessThan(LocalDateTime.now()).andDeletedEqualTo(false)
        return couponUserMapper.selectByExample(example)
    }
}