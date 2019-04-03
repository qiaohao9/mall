package me.vastpeng.mall.admin.util

import me.vastpeng.mall.admin.annotation.RequiresPermissionsDesc
import org.apache.shiro.authz.annotation.RequiresPermissions


class Permission {
    var requiresPermissions: RequiresPermissions? = null
    var requiresPermissionsDesc: RequiresPermissionsDesc? = null
    var api: String? = null
}