package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallOrderMapper
import me.vastpeng.mall.db.dao.OrderMapper
import me.vastpeng.mall.db.domain.MallOrder
import me.vastpeng.mall.db.domain.MallOrderExample
import me.vastpeng.mall.db.util.OrderUtil
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.Resource
import kotlin.collections.HashMap


@Service
class MallOrderService {
    @Resource
    private var mallOrderMapper: MallOrderMapper? = null
    @Resource
    private var orderMapper: OrderMapper? = null

    fun count(userId: Int): Int {
        var example = MallOrderExample()
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false)
        return mallOrderMapper!!.countByExample(example).toInt()
    }

    fun findById(orderId: Int): MallOrder {
        return mallOrderMapper!!.selectByPrimaryKey(orderId)
    }

    private fun getRandomNum(num: Int): String {
        var base = "0123456789"
        var random = Random()
        var sb = StringBuffer()
        for (i in 0 until num) {
            sb.append(base[random.nextInt(base.length)])
        }

        return sb.toString()
    }

    fun countByOrderSn(userId: Int, orderSn: String): Int {
        var example = MallOrderExample()
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false)
        return mallOrderMapper!!.countByExample(example).toInt()
    }

    // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
    fun generateOrderSn(userId: Int): String {
        var df = DateTimeFormatter.ofPattern("yyyyMMdd")
        var now = df.format(LocalDate.now())
        var orderSn = now + this.getRandomNum(6)
        while (this.countByOrderSn(userId, orderSn) != 0) {
            orderSn = this.getRandomNum(6)
        }

        return orderSn
    }

    fun queryByOrderStatus(userId: Int, orderStatus: List<Short>, page: Int, size: Int): List<MallOrder> {
        var example = MallOrderExample()
        example.orderByClause = MallOrder.Column.addTime.desc()
        var criteria = example.createCriteria()
        criteria.andUserIdEqualTo(userId)
        if (orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus)
        }
        criteria.andDeletedEqualTo(false)
        PageHelper.startPage<Int>(page, size)
        return mallOrderMapper!!.selectByExample(example)
    }

    fun querySelective(userId: Int, orderSn: String, orderStatusArray: List<Short>, page: Int, size: Int, sort: String, order: String): List<MallOrder> {
        var example = MallOrderExample()
        var criteria = example.createCriteria()

        if (userId != null) {
            criteria.andUserIdEqualTo(userId)
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnEqualTo(orderSn)
        }
        if (orderStatusArray != null && orderStatusArray.isNotEmpty()) {
            criteria.andOrderStatusIn(orderStatusArray)
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, size)
        return mallOrderMapper!!.selectByExample(example)
    }

    fun updateWithOptimisticLocker(order: MallOrder): Int {
        var preUpdateTime = order.updateTime
        order.updateTime = LocalDateTime.now()
        return orderMapper!!.updateWithOptimisticLocker(preUpdateTime, order)
    }

    fun deleteById(id: Int) {
        mallOrderMapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun count(): Int {
        var example = MallOrderExample()
        example.or().andDeletedEqualTo(false)
        return mallOrderMapper!!.countByExample(example).toInt()
    }

    fun queryUnpaid(): List<MallOrder> {
        var example = MallOrderExample()
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andDeletedEqualTo(false)
        return mallOrderMapper!!.selectByExample(example)
    }

    fun queryUnconfirm(): List<MallOrder> {
        var example = MallOrderExample()
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeIsNotNull().andDeletedEqualTo(false)
        return mallOrderMapper!!.selectByExample(example)
    }

    fun findBySn(orderSn: String): MallOrder {
        var example = MallOrderExample()
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false)
        return mallOrderMapper!!.selectOneByExample(example)
    }

    fun orderInfo(userId: Int): Map<Any, Any> {
        var example = MallOrderExample()
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false)
        var orders = mallOrderMapper!!.selectByExampleSelective(example, MallOrder.Column.orderStatus, MallOrder.Column.comments)

        var unpaid = 0
        var unship = 0
        var unrecv = 0
        var uncomment = 0

        for (order in orders) {
            if (OrderUtil.isCreateStatus(order)) {
                unpaid++
            } else if (OrderUtil.isPayStatus(order)) {
                unship++
            } else if (OrderUtil.isShipStatus(order)) {
                unrecv++
            } else if (OrderUtil.isConfirmStatus(order) || OrderUtil.isAutoCancelStatus(order)) {
                uncomment += order.comments
            } else {

            }
        }

        var orderInfo = HashMap<Any, Any>()
        orderInfo["unpaid"] = unpaid
        orderInfo["unship"] = unship
        orderInfo["unrecv"] = unrecv
        orderInfo["uncomment"] = uncomment
        return orderInfo
    }

    fun queryComment(): List<MallOrder> {
        var example = MallOrderExample()
        example.or().andCommentsGreaterThan(0.toShort()).andDeletedEqualTo(false)
        return mallOrderMapper!!.selectByExample(example)

    }
}