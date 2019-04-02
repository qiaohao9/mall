package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallAdminMapper
import me.vastpeng.mall.db.domain.MallAdmin
import me.vastpeng.mall.db.domain.MallAdminExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallAdminService(private val result: Array<MallAdmin.Column> = arrayOf(MallAdmin.Column.id, MallAdmin.Column.username, MallAdmin.Column.avatar, MallAdmin.Column.roleIds)) {
    @Resource
    private var adminMapper: MallAdminMapper? = null

    fun findAdmin(userName: String): List<MallAdmin> {
        var example: MallAdminExample = MallAdminExample()
        example.or().andUsernameEqualTo(userName).andDeletedEqualTo(false)
        return adminMapper!!.selectByExample(example)
    }

    fun findAdmin(id: Int): MallAdmin {
        return adminMapper!!.selectByPrimaryKey(id)
    }

    fun querySelective(userName: String, page: Int, limit: Int, sort: String, order: String): List<MallAdmin> {
        var example: MallAdminExample = MallAdminExample()
        var criteria: MallAdminExample.Criteria = example.createCriteria()

        if (!StringUtils.isEmpty(userName)) {
            criteria.andUsernameLike("%$userName%")
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, limit)
        return adminMapper!!.selectByExampleSelective(example, *result)
    }

    fun updateById(admin: MallAdmin): Int {
        admin.updateTime = LocalDateTime.now()
        return adminMapper!!.updateByPrimaryKeySelective(admin)
    }

    fun deleteById(id: Int) {
        adminMapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun add(admin: MallAdmin) {
        admin.addTime = LocalDateTime.now()
        admin.updateTime = LocalDateTime.now()
        adminMapper!!.insertSelective(admin)
    }

    fun findById(id: Int): MallAdmin {
        return adminMapper!!.selectByPrimaryKeySelective(id, *result)
    }
}