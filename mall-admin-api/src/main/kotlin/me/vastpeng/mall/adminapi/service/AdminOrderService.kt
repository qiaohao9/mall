package me.vastpeng.mall.adminapi.service

import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import me.vastpeng.mall.core.notify.NotifyService
import com.github.binarywang.wxpay.service.WxPayService
import com.github.pagehelper.PageInfo
import me.vastpeng.mall.core.util.ResponseUtil
import me.vastpeng.mall.db.service.*
import org.springframework.beans.factory.annotation.Autowired
import java.util.HashMap
import com.github.binarywang.wxpay.exception.WxPayException
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult
import java.math.BigDecimal
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest
import me.vastpeng.mall.adminapi.util.AdminResponseCode.ORDER_CONFIRM_NOT_ALLOWED
import me.vastpeng.mall.adminapi.util.AdminResponseCode.ORDER_REFUND_FAILED
import me.vastpeng.mall.adminapi.util.AdminResponseCode.ORDER_REPLY_EXIST
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import me.vastpeng.mall.db.util.OrderUtil
import me.vastpeng.mall.core.util.JacksonUtil
import me.vastpeng.mall.db.domain.MallComment


@Service
class AdminOrderService {
    private val logger = LogFactory.getLog(AdminOrderService::class.java)

    @Autowired
    private lateinit var orderGoodsService: MallOrderGoodsService
    @Autowired
    private lateinit var orderService: MallOrderService
    @Autowired
    private lateinit var productService: MallGoodsProductService
    @Autowired
    private lateinit var userService: MallUserService
    @Autowired
    private lateinit var commentService: MallCommentService
    @Autowired
    private lateinit var wxPayService: WxPayService
    @Autowired
    private lateinit var notifyService: NotifyService

    fun list(userId: Int, orderSn: String, orderStatusArray: List<Short>, page: Int, limit: Int, sort: String, order: String): Any {
        var orderList = orderService.querySelective(userId, orderSn, orderStatusArray, page, limit, sort, order)
        var total = PageInfo.of(orderList).total

        var data = HashMap<String, Any>()
        data["total"] = total
        data["items"] = orderList

        return ResponseUtil.ok(data)
    }

    fun detail(id: Int): Any {
        val order = orderService.findById(id)
        val orderGoods = orderGoodsService.queryByOid(id)
        val user = userService.findUserVoById(order?.userId) ?: null
        val data = HashMap<String, Any>()
        data["order"] = order!!
        data["orderGoods"] = orderGoods
        data["user"] = user!!


        return ResponseUtil.ok(data)
    }

    /**
     * 订单退款
     *
     *
     * 1. 检测当前订单是否能够退款;
     * 2. 微信退款操作;
     * 3. 设置订单退款确认状态；
     * 4. 订单商品库存回库。
     *
     *
     * TODO
     * 虽然接入了微信退款API，但是从安全角度考虑，建议开发者删除这里微信退款代码，采用以下两步走步骤：
     * 1. 管理员登录微信官方支付平台点击退款操作进行退款
     * 2. 管理员登录Mall管理后台点击退款操作进行订单状态修改和商品库存回库
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @Transactional
    fun refund(body: String): Any {
        val orderId = JacksonUtil.parseInteger(body, "orderId")
        val refundMoney = JacksonUtil.parseString(body, "refundMoney")
        if (orderId == null) {
            return ResponseUtil.badArgument()
        }
        if (StringUtils.isEmpty(refundMoney)) {
            return ResponseUtil.badArgument()
        }

        val order = orderService.findById(orderId) ?: return ResponseUtil.badArgument()

        if (order.actualPrice.compareTo(BigDecimal(refundMoney)) !== 0) {
            return ResponseUtil.badArgumentValue()
        }

        // 如果订单不是退款状态，则不能退款
        if (order.orderStatus != OrderUtil.STATUS_REFUND) {
            return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不能确认收货")
        }

        // 微信退款
        val wxPayRefundRequest = WxPayRefundRequest()
        wxPayRefundRequest.outTradeNo = order.orderSn
        wxPayRefundRequest.outRefundNo = "refund_" + order.orderSn
        // 元转成分
        val totalFee = order.actualPrice.multiply(BigDecimal(100)).toInt()
        wxPayRefundRequest.totalFee = totalFee
        wxPayRefundRequest.refundFee = totalFee

        var wxPayRefundResult: WxPayRefundResult? = null
        try {
            wxPayRefundResult = wxPayService.refund(wxPayRefundRequest)
        } catch (e: WxPayException) {
            e.printStackTrace()
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败")
        }

        if (wxPayRefundResult!!.returnCode != "SUCCESS") {
            logger.warn("refund fail: " + wxPayRefundResult.returnMsg)
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败")
        }
        if (wxPayRefundResult.resultCode != "SUCCESS") {
            logger.warn("refund fail: " + wxPayRefundResult.returnMsg)
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败")
        }

        // 设置订单取消状态
        order.orderStatus = OrderUtil.STATUS_REFUND_CONFIRM
        if (orderService.updateWithOptimisticLocker(order) === 0) {
            throw RuntimeException("更新数据已失效")
        }

        // 商品货品数量增加
        val orderGoodsList = orderGoodsService.queryByOid(orderId)
        for (orderGoods in orderGoodsList) {
            val productId = orderGoods.productId
            val number = orderGoods.number
            if (productService.addStock(productId, number.toInt()) === 0) {
                throw RuntimeException("商品货品库存增加失败")
            }
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
        // 注意订单号只发后6位
//        notifyService.notifySmsTemplate(order.mobile, NotifyType.REFUND, arrayOf(order.orderSn.substring(8, 14)))
        return ResponseUtil.ok()
    }

    /**
     * 发货
     * 1. 检测当前订单是否能够发货
     * 2. 设置订单发货状态
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    fun ship(body: String): Any {
        val orderId = JacksonUtil.parseInteger(body, "orderId")
        val shipSn = JacksonUtil.parseString(body, "shipSn")
        val shipChannel = JacksonUtil.parseString(body, "shipChannel")
        if (orderId == null || shipSn == null || shipChannel == null) {
            return ResponseUtil.badArgument()
        }

        val order = orderService.findById(orderId) ?: return ResponseUtil.badArgument()

        // 如果订单不是已付款状态，则不能发货
        if (order.orderStatus != OrderUtil.STATUS_PAY) {
            return ResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED, "订单不能确认收货")
        }

        order.orderStatus = OrderUtil.STATUS_SHIP
        order.shipSn = shipSn
        order.shipChannel = shipChannel
        order.shipTime = LocalDateTime.now()
        if (orderService.updateWithOptimisticLocker(order) === 0) {
            return ResponseUtil.updatedDateExpired()
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 发货会发送通知短信给用户:          *
        // "您的订单已经发货，快递公司 {1}，快递单 {2} ，请注意查收"
//        notifyService.notifySmsTemplate(order.mobile, NotifyType.SHIP, arrayOf(shipChannel, shipSn))

        return ResponseUtil.ok()
    }

    /**
     * 回复订单商品
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    fun reply(body: String): Any {
        val commentId = JacksonUtil.parseInteger(body, "commentId")
        if (commentId == null || commentId == 0) {
            return ResponseUtil.badArgument()
        }
        // 目前只支持回复一次
        if (commentService.findById(commentId) != null) {
            return ResponseUtil.fail(ORDER_REPLY_EXIST, "订单商品已回复！")
        }
        val content = JacksonUtil.parseString(body, "content")
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument()
        }
        // 创建评价回复
        val comment = MallComment()
        comment.type = 2.toByte()
        comment.valueId = commentId
        comment.content = content
        comment.userId = 0                 // 评价回复没有用
        comment.star = 0.toShort()           // 评价回复没有用
        comment.hasPicture = false        // 评价回复没有用
        comment.picUrls = arrayOf<String>()  // 评价回复没有用
        commentService.save(comment)

        return ResponseUtil.ok()
    }


}