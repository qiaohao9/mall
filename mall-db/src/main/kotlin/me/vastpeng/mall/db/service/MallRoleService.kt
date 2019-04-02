package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallRoleMapper
import me.vastpeng.mall.db.domain.MallRole
import me.vastpeng.mall.db.domain.MallRoleExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallRoleService {
    @Resource
    private var roleMapper: MallRoleMapper? = null

    fun queryByIds(roleIds: Array<Int>): Set<String> {
        var roles = HashSet<String>()
        if (roles.size == 0) {
            return roles
        }

        var example = MallRoleExample()
        example.or().andIdIn(roleIds.toMutableList()).andEnabledEqualTo(true).andDeletedEqualTo(false);
        var roleList = roleMapper!!.selectByExample(example)

        for (role in roleList) {
            roles.add(role.name)
        }

        return roles
    }

    fun querySelective(roleName: String, page: Int, size: Int, sort: String, order: String): List<MallRole> {
        var example = MallRoleExample()
        var criteria = example.createCriteria()


        if (!StringUtils.isEmpty(roleName)) {
            criteria.andNameEqualTo("%$roleName%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, size);
        return roleMapper!!.selectByExample(example);
    }

    fun findById(id: Int): MallRole {
        return roleMapper!!.selectByPrimaryKey(id);
    }

    fun add(role: MallRole) {
        role.addTime = LocalDateTime.now()
        role.updateTime = LocalDateTime.now()
        roleMapper!!.insertSelective(role)
    }

    fun deleteById(id: Int) {
        roleMapper!!.logicalDeleteByPrimaryKey(id);
    }

    fun updateById(role: MallRole) {
        role.updateTime = LocalDateTime.now()
        roleMapper!!.updateByPrimaryKeySelective(role)
    }

    fun checkExist(name: String): Boolean {
        var example = MallRoleExample()
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return roleMapper!!.countByExample(example) != 0L;
    }

    fun queryAll(): List<MallRole> {
        var example = MallRoleExample()
        example.or().andDeletedEqualTo(false);
        return roleMapper!!.selectByExample(example);
    }
}