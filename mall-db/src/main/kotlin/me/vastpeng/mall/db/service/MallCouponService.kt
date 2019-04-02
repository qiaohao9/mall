package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallCouponMapper
import me.vastpeng.mall.db.dao.MallCouponUserMapper
import me.vastpeng.mall.db.domain.MallCoupon
import me.vastpeng.mall.db.domain.MallCouponExample
import me.vastpeng.mall.db.domain.MallCouponUser
import me.vastpeng.mall.db.domain.MallCouponUserExample
import me.vastpeng.mall.db.util.CouponConstant
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Resource

@Service
class MallCouponService {
    @Resource
    private var couponMapper: MallCouponMapper? = null
    @Resource
    private var couponUserMapper: MallCouponUserMapper? = null

    private var result: Array<MallCoupon.Column> = arrayOf(MallCoupon.Column.id, MallCoupon.Column.name, MallCoupon.Column.desc, MallCoupon.Column.tag, MallCoupon.Column.days, MallCoupon.Column.startTime, MallCoupon.Column.endTime, MallCoupon.Column.discount, MallCoupon.Column.min)

    fun queryList(offset: Int, limit: Int, sort: String, order: String): MutableList<MallCoupon>? {
        return this.queryList(MallCouponExample.newAndCreateCriteria(), offset, limit, sort, order)
    }

    fun queryList(criteria: MallCouponExample.Criteria, offset: Int, limit: Int, sort: String, order: String): MutableList<MallCoupon>? {
        criteria.andTypeEqualTo(CouponConstant.TYPE_COMMON).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false)
        criteria.example().orderByClause = "$sort $order"
        PageHelper.startPage<Int>(offset, limit)
        return couponMapper!!.selectByExampleSelective(criteria.example(), *result)
    }

    fun queryTotal(): Int {
        var example: MallCouponExample = MallCouponExample()
        example.or().andTypeEqualTo(CouponConstant.TYPE_COMMON).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false)
        return couponMapper!!.countByExample(example).toInt()
    }

    fun queryAvailableList(userId: Int, offset: Int, limit: Int): MutableList<MallCoupon>? {
        assert(userId != null)
        // 过滤掉登录账号已经领取过的coupon
        var c: MallCouponExample.Criteria = MallCouponExample.newAndCreateCriteria()
        var used: List<MallCouponUser> = couponUserMapper!!.selectByExample(MallCouponUserExample.newAndCreateCriteria().andUserIdEqualTo(userId).example())
        if (used != null && !used.isEmpty()) {
            c.andIdNotIn(used.stream().map { it.couponId }.collect(Collectors.toList()))
        }
        return queryList(c, offset, limit, "add_time", "desc")
    }

    fun queryList(offset: Int, limit: Int): MutableList<MallCoupon>? {
        return queryList(offset, limit, "add_time", "desc")
    }

    fun findById(id: Int): MallCoupon {
        return couponMapper!!.selectByPrimaryKey(id)
    }

    fun findByCode(code: String): MallCoupon? {
        var example: MallCouponExample = MallCouponExample()
        example.or().andCodeEqualTo(code).andTypeEqualTo(CouponConstant.TYPE_CODE).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false)
        var couponList: List<MallCoupon> = couponMapper!!.selectByExample(example)
        return when {
            couponList.size > 1 -> throw RuntimeException("")
            couponList.isEmpty() -> null
            else -> couponList[0]
        }
    }

    fun queryRegister(): List<MallCoupon> {
        var example: MallCouponExample = MallCouponExample()
        example.or().andTypeEqualTo(CouponConstant.TYPE_REGISTER).andStatusEqualTo(CouponConstant.STATUS_NORMAL).andDeletedEqualTo(false)
        return couponMapper!!.selectByExample(example)
    }

    fun querySelective(name: String, type: Short, status: Short, page: Int, limit: Int, sort: String, order: String): List<MallCoupon> {
        var example: MallCouponExample = MallCouponExample()
        var criteria: MallCouponExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameEqualTo("%$name%")
        }
        if (type != null) {
            criteria.andTypeEqualTo(type)
        }
        if (status != null) {
            criteria.andStatusEqualTo(status)
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, limit)
        return couponMapper!!.selectByExample(example)
    }

    fun add(coupon: MallCoupon) {
        coupon.addTime = LocalDateTime.now()
        coupon.updateTime = LocalDateTime.now()
        couponMapper!!.insertSelective(coupon)
    }

    fun updateById(coupon: MallCoupon): Int {
        coupon.updateTime = LocalDateTime.now()
        return couponMapper!!.updateByPrimaryKeySelective(coupon)
    }

    fun deleteById(id: Int) {
        couponMapper!!.logicalDeleteByPrimaryKey(id)
    }

    private fun getRandomNum(num: Int): String {
        var base: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var random: Random = Random()
        var sb: StringBuffer = StringBuffer()
        for (i in 0 until num) {
            var number: Int = random.nextInt(base.length)
            sb.append(base[number])
        }
        return sb.toString()
    }

    fun generateCode(): String {
        var code: String = this.getRandomNum(8)
        while (findByCode(code) != null) {
            code = this.getRandomNum(8)
        }
        return code
    }

    fun queryExpired(): List<MallCoupon> {
        var example: MallCouponExample = MallCouponExample()
        example.or().andStatusEqualTo(CouponConstant.STATUS_NORMAL).andTimeTypeEqualTo(CouponConstant.TIME_TYPE_TIME).andDeletedEqualTo(false)
        return couponMapper!!.selectByExample(example)
    }

}