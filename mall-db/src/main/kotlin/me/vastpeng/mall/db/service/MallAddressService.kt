package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallAddressMapper
import me.vastpeng.mall.db.domain.MallAddress
import me.vastpeng.mall.db.domain.MallAddressExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallAddressService {
    @Resource
    private lateinit var addressMapper: MallAddressMapper

    fun queryByUid(uid: Int): MutableList<MallAddress>? {
        var example: MallAddressExample = MallAddressExample()
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false)
        return addressMapper.selectByExample(example)
    }

    fun findById(id: Int): MallAddress {
        return addressMapper.selectByPrimaryKey(id)
    }

    fun add(address: MallAddress): Int {
        address.addTime = LocalDateTime.now()
        address.updateTime = LocalDateTime.now()
        return addressMapper.insertSelective(address)
    }

    fun update(address: MallAddress): Int {
        address.updateTime = LocalDateTime.now()
        return addressMapper.updateByPrimaryKeySelective(address)
    }

    fun delete(id: Int) {
        addressMapper.logicalDeleteByPrimaryKey(id)
    }

    fun findDefault(userId: Int): MallAddress {
        var example: MallAddressExample = MallAddressExample()
        example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false)
        return addressMapper.selectOneByExample(example)
    }

    fun resetDefault(userId: Int) {
        var address: MallAddress = MallAddress()
        address.isDefault = false
        address.updateTime = LocalDateTime.now()

        var example: MallAddressExample = MallAddressExample()
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false)
        addressMapper.updateByExampleSelective(address, example)
    }

    fun querySelective(userId: Int, name: String, page: Int, limit: Int, sort: String, order: String): List<MallAddress> {
        var example: MallAddressExample = MallAddressExample()
        var criteria: MallAddressExample.Criteria = example.createCriteria()

        if (userId != null) {
            criteria.andUserIdEqualTo(userId)
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%$name%")
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, limit)
        return addressMapper.selectByExample(example)
    }
}