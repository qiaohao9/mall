package me.vastpeng.mall.adminapi.util

import me.vastpeng.mall.adminapi.annotation.RequiresPermissionsDesc
import org.apache.shiro.authz.annotation.RequiresPermissions


class Permission {
    var requiresPermissions: RequiresPermissions? = null
    var requiresPermissionsDesc: RequiresPermissionsDesc? = null
    var api: String? = null
}