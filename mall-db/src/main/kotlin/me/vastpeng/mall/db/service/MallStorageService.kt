package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallStorageMapper
import me.vastpeng.mall.db.domain.MallStorage
import me.vastpeng.mall.db.domain.MallStorageExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallStorageService {
    @Resource
    private var storageMapper: MallStorageMapper? = null

    fun deleteByKey(key: String) {
        var example = MallStorageExample()
        example.or().andKeyEqualTo(key)
        storageMapper!!.logicalDeleteByExample(example)
    }

    fun add(storageInfo: MallStorage) {
        storageInfo.addTime = LocalDateTime.now()
        storageInfo.updateTime = LocalDateTime.now()
        this.storageMapper!!.insertSelective(storageInfo)
    }

    fun findByKey(key: String): MallStorage {
        var example = MallStorageExample()
        example.or().andKeyEqualTo(key).andDeletedEqualTo(false)
        return storageMapper!!.selectOneByExample(example)
    }

    fun update(storageInfo: MallStorage): Int {
        storageInfo.updateTime = LocalDateTime.now()
        return storageMapper!!.updateByPrimaryKeySelective(storageInfo)
    }

    fun findById(id: Int): MallStorage {
        return storageMapper!!.selectByPrimaryKey(id)
    }

    fun querySelective(key: String, name: String, page: Int, limit: Int, sort: String, order: String): List<MallStorage> {
        var example = MallStorageExample()
        var criteria = example.createCriteria()

        if (!StringUtils.isEmpty(key)) {
            criteria.andKeyEqualTo(key)
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%$name%")
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, limit)
        return storageMapper!!.selectByExample(example)
    }
}