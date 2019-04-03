package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.MallPermissionMapper
import me.vastpeng.mall.db.domain.MallPermission
import me.vastpeng.mall.db.domain.MallPermissionExample
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallPermissionService {
    @Resource
    private var permissionMapper: MallPermissionMapper? = null

    fun queryByRoleIds(roleIds: Array<Int>): Set<String> {
        var permissions = HashSet<String>()
        if (roleIds.isEmpty()) {
            return permissions
        }

        var example = MallPermissionExample()
        example.or().andRoleIdIn(roleIds.toMutableList()).andDeletedEqualTo(false)
        var permissionList = permissionMapper!!.selectByExample(example)

        for (permission in permissionList) {
            permissions.add(permission.permission)
        }

        return permissions
    }

    fun queryByRoleId(roleId: Int): Set<String> {
        var permissions = HashSet<String>()
        if (roleId == null) {
            return permissions
        }

        var example = MallPermissionExample()
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false)

        var permissionList = permissionMapper!!.selectByExample(example)
        for (permission in permissionList) {
            permissions.add(permission.permission)
        }

        return permissions
    }

    fun checkSuperPermission(roleId: Int): Boolean {
        if (roleId == null) {
            return false
        }

        var example = MallPermissionExample()
        example.or().andRoleIdEqualTo(roleId).andPermissionEqualTo("*").andDeletedEqualTo(false)
        return permissionMapper!!.countByExample(example) != 0L
    }

    fun deleteByRoleId(roleId: Int) {
        var example = MallPermissionExample()
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false)
        permissionMapper!!.logicalDeleteByExample(example)
    }

    fun add(mallPermission: MallPermission) {
        mallPermission.addTime = LocalDateTime.now()
        mallPermission.updateTime = LocalDateTime.now()
        permissionMapper!!.insertSelective(mallPermission)
    }
}