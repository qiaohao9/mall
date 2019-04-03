package me.vastpeng.mall.admin.shiro

import me.vastpeng.mall.core.util.bcrypt.BCryptPasswordEncoder
import me.vastpeng.mall.db.domain.MallAdmin
import me.vastpeng.mall.db.service.MallAdminService
import me.vastpeng.mall.db.service.MallPermissionService
import me.vastpeng.mall.db.service.MallRoleService
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.util.Assert
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.util.StringUtils

@Configuration
class AdminAuthorizingRealm : AuthorizingRealm() {

    companion object {
        private val log = LoggerFactory.getLogger(AdminAuthorizingRealm::class.java)
    }

    @Autowired
    private lateinit var adminService: MallAdminService
    @Autowired
    private lateinit var roleService: MallRoleService
    @Autowired
    private lateinit var permissionService: MallPermissionService

    override fun doGetAuthenticationInfo(token: AuthenticationToken?): AuthenticationInfo {
        var upToken = token as UsernamePasswordToken
        var username = upToken.username
        var password = String(upToken.password)

        if (StringUtils.isEmpty(username)) {
            throw AccountException("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw AccountException("密码不能为空")
        }

        var adminList = adminService.findAdmin(username)
        Assert.state(adminList.size < 2, "同一个用户名存在两个账户")
        if (adminList.isEmpty()) {
            throw UnknownAccountException("找不到用户($username)的账号信息")
        }
        var admin = adminList[0]

        var encoder = BCryptPasswordEncoder()
        if (!encoder.matches(password, admin.password)) {
            throw UnknownAccountException("找不到用户($username)的账号信息")
        }

        return SimpleAuthenticationInfo(admin, password, name)
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection?): AuthorizationInfo {
        if (principals == null) {
            throw AuthorizationException("PrincipalCollection method argument cannot be null.")
        }

        val admin = getAvailablePrincipal(principals) as MallAdmin
        var roleIds = admin.roleIds
        var roles = roleService.queryByIds(roleIds)
        var permissions = permissionService.queryByRoleIds(roleIds)
        var info = SimpleAuthorizationInfo()
        info.roles = roles
        info.stringPermissions = permissions
        return info
    }

}