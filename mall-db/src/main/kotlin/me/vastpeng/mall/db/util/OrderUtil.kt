package me.vastpeng.mall.db.util

import me.vastpeng.mall.db.domain.MallOrder
import java.lang.IllegalStateException

/*
 * 订单流程：下单成功－》支付订单－》发货－》收货
 * 订单状态：
 * 101 订单生成，未支付；102，下单未支付用户取消；103，下单未支付超期系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，用户申请退款；203，管理员执行退款操作，确认退款成功；
 * 301 商家发货，用户未确认；
 * 401 用户确认收货，订单结束； 402 用户没有确认收货，但是快递反馈已收获后，超过一定时间，系统自动确认收货，订单结束。
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消或者付款
 * 当201支付完成而商家未发货时，此时用户可以退款
 * 当301商家已发货时，此时用户可以有确认收货
 * 当401用户确认收货以后，此时用户可以进行的操作是退货、删除、去评价或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除、去评价、或者再次购买
 */
class OrderUtil {
    companion object {
        const val STATUS_CREATE: Short = 101
        const val STATUS_PAY: Short = 201
        const val STATUS_SHIP: Short = 301
        const val STATUS_CONFIRM: Short = 401
        const val STATUS_CANCEL: Short = 102
        const val STATUS_AUTO_CANCEL: Short = 103
        const val STATUS_REFUND: Short = 202
        const val STATUS_REFUND_CONFIRM: Short = 203
        const val STATUS_AUTO_CONFIRM: Short = 402

        fun orderStatusText(order: MallOrder): String = when (order.orderStatus.toShort()) {
            STATUS_CREATE -> "未付款"
            STATUS_CANCEL -> "已取消"
            STATUS_AUTO_CANCEL -> "已取消（系统）"
            STATUS_PAY -> "已付款"
            STATUS_REFUND -> "订单取消，退款中"
            STATUS_REFUND_CONFIRM -> "已退款"
            STATUS_SHIP -> "已发货"
            STATUS_CONFIRM -> "已收货"
            STATUS_AUTO_CONFIRM -> "已收货（系统)"
            else -> throw IllegalStateException("orderStatus 不支持")
        }

        fun build(order: MallOrder): OrderHandlerOption {
            var handlerOption = OrderHandlerOption()
            when (order.orderStatus.toShort()) {
                // 如果订单没有被取消，且没有支付，则可支付，可取消
                STATUS_CREATE -> {
                    handlerOption.cancel = true
                    handlerOption.pay = true
                }
                // 如果订单已经取消或是已完成，则可删除
                STATUS_CANCEL, STATUS_AUTO_CANCEL -> handlerOption.delete = true
                // 如果订单已付款，没有发货，则可退款
                STATUS_PAY -> handlerOption.refund = true
                // 如果订单申请退款中，没有相关操作
                STATUS_REFUND -> {
                }
                // 如果订单已经退款，则可删除
                STATUS_REFUND_CONFIRM -> handlerOption.delete = true
                // 如果订单已经发货，没有收货，则可收货操作，此时不能取消订单
                STATUS_SHIP -> handlerOption.confirm = true
                // 如果订单已经支付，且已经收货，则可删除、去评论和再次购买
                STATUS_CONFIRM, STATUS_AUTO_CONFIRM -> {
                    handlerOption.delete = true
                    handlerOption.comment = true
                    handlerOption.rebuy = true
                }
                else -> {
                    throw IllegalStateException("status 不支持")
                }
            }

            return handlerOption
        }

        fun orderStatus(showType: Int): List<Short>? {
            // 全部订单
            if (showType == 0) {
                return null
            }

            var status = ArrayList<Short>(2)
            when (showType) {
                // 待付款订单
                1 -> status.add(STATUS_CREATE)
                // 待发货订单
                2 -> status.add(STATUS_PAY)
                // 待收货订单
                3 -> status.add(STATUS_SHIP)
                // 待评价订单
                4 -> status.add(STATUS_CONFIRM)
                else -> return null
            }

            return status
        }

        fun isCreateStatus(mallOrder: MallOrder) = OrderUtil.STATUS_CREATE == mallOrder.orderStatus.toShort()
        fun isPayStatus(mallOrder: MallOrder) = OrderUtil.STATUS_PAY == mallOrder.orderStatus.toShort()
        fun isShipStatus(mallOrder: MallOrder) = OrderUtil.STATUS_SHIP == mallOrder.orderStatus.toShort()
        fun isConfirmStatus(mallOrder: MallOrder) = OrderUtil.STATUS_CONFIRM == mallOrder.orderStatus.toShort()
        fun isCancelStatus(mallOrder: MallOrder) = OrderUtil.STATUS_CANCEL == mallOrder.orderStatus.toShort()
        fun isAutoCancelStatus(mallOrder: MallOrder) = OrderUtil.STATUS_AUTO_CANCEL == mallOrder.orderStatus.toShort()
        fun isRefundStatus(mallOrder: MallOrder) = OrderUtil.STATUS_REFUND == mallOrder.orderStatus.toShort()
        fun isRefundConfirmStatus(mallOrder: MallOrder) = OrderUtil.STATUS_REFUND_CONFIRM == mallOrder.orderStatus.toShort()
        fun isAutoConfirmStatus(mallOrder: MallOrder) = OrderUtil.STATUS_AUTO_CONFIRM == mallOrder.orderStatus.toShort()
    }
}

