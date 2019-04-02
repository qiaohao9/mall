package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.domain.MallCoupon
import me.vastpeng.mall.db.domain.MallCouponUser
import me.vastpeng.mall.db.util.CouponConstant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class CouponVerifyService {
    @Autowired
    private var couponUserService: MallCouponUserService? = null
    @Autowired
    private lateinit var couponService: MallCouponService

    fun checkCoupon(userID: Int, couponId: Int, checkedGoodsPrice: BigDecimal): MallCoupon? {
        var coupon: MallCoupon = couponService.findById(couponId)
        var couponUser: MallCouponUser? = couponUserService!!.queryOne(userID, couponId)
        if (coupon == null || couponUser == null) {
            return null
        }

        // 检查是否超期
        var timeType: Short = coupon.timeType
        var days: Short = coupon.days
        var now: LocalDateTime = LocalDateTime.now()
        when (timeType) {
            CouponConstant.TIME_TYPE_TIME -> {
                if (now.isBefore(coupon.startTime) || now.isAfter(coupon.endTime)) {
                    return null
                }
            }
            CouponConstant.TIME_TYPE_DAYS -> {
                var expired: LocalDateTime = couponUser.addTime.plusDays(days.toLong())
                if (now.isAfter(expired)) {
                    return null
                }
            }
            else -> {

                return null
            }
        }

        // 检测商品是否符合
        // TODO 目前仅支持全平台商品，所以不需要检测
        var goodType: Short = coupon.goodsType
        if (goodType != CouponConstant.GOODS_TYPE_ALL) {
            return null
        }

        // 检测订单状态
        var status: Short = coupon.status
        if (status != CouponConstant.STATUS_NORMAL) {
            return null
        }

        // 检测是否满足最低消费
        if (checkedGoodsPrice.compareTo(coupon.min) == -1) {
            return null
        }

        return coupon
    }
}