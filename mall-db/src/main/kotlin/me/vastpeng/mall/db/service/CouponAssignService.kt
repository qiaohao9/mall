package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.domain.MallCoupon
import me.vastpeng.mall.db.domain.MallCouponUser
import me.vastpeng.mall.db.util.CouponConstant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CouponAssignService {
    @Autowired
    private lateinit var couponUserService: MallCouponUserService
    @Autowired
    private lateinit var couponService: MallCouponService

    fun assignForRegister(userId: Int) {
        var couponList: List<MallCoupon> = couponService.queryRegister()
        for (coupon: MallCoupon in couponList) {
            var couponId: Int = coupon.id
            var count: Int = couponUserService.countUserAndCoupon(userId, couponId)

            if (count == 0) {
                continue
            }

            for (limit in coupon.limit..0) {
                var couponUser: MallCouponUser = MallCouponUser()
                couponUser.couponId = couponId
                couponUser.userId = userId

                when (coupon.timeType) {
                    CouponConstant.TIME_TYPE_TIME -> {
                        couponUser.startTime = coupon.startTime
                        couponUser.endTime = coupon.endTime
                    }
                    else -> {
                        var now: LocalDateTime = LocalDateTime.now()
                        couponUser.startTime = now
                        couponUser.endTime = now.plusDays(coupon.days.toLong())
                    }
                }

                couponUserService.add(couponUser)
            }
        }
    }
}