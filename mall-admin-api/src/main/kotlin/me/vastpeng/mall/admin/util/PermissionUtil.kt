package me.vastpeng.mall.admin.util

import java.util.ArrayList
import me.vastpeng.mall.admin.annotation.RequiresPermissionsDesc
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.reflect.MethodUtils
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Controller
import org.springframework.util.ClassUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.HashSet


class PermissionUtil {
    companion object {
        fun listPermVo(permissions: List<Permission>): List<PermVo> {
            var root = ArrayList<PermVo>()

            for (permission in permissions) {
                var requiresPermissions = permission.requiresPermissions
                var requiresPermissionsDesc = permission.requiresPermissionsDesc
                var api = permission.api

                val menus = requiresPermissionsDesc!!.menu
                if (menus.size != 2) {
                    throw RuntimeException("目前只支持两级菜单")
                }

                val menu1 = menus[0]
                var perm1: PermVo? = null
                for (permVo in root) {
                    if (permVo.label == menu1) {
                        perm1 = permVo
                        break
                    }
                }
                if (perm1 == null) {
                    perm1 = PermVo()
                    perm1.id = menu1
                    perm1.label = menu1
                    perm1.children = ArrayList()
                    root.add(perm1)
                }

                val menu2 = menus[1]
                var perm2: PermVo? = null
                for (permVo in perm1.children) {
                    if (permVo.label == menu2) {
                        perm2 = permVo
                        break
                    }
                }
                if (perm2 == null) {
                    perm2 = PermVo()
                    perm2.id = menu2
                    perm2.label = menu2
                    perm2.children = ArrayList()
                    perm1.children.add(perm2)
                }


                val button = requiresPermissionsDesc.button
                var leftPerm: PermVo? = null
                for (permVo in perm2.children) {
                    if (permVo.label == button) {
                        leftPerm = permVo
                        break
                    }
                }

                if (leftPerm == null) {
                    leftPerm = PermVo()
                    leftPerm.id = requiresPermissions!!.value[0]
                    leftPerm.label = requiresPermissionsDesc.button
                    leftPerm.api = api
                    perm2.children.add(leftPerm)
                } else {
                    // TODO
                    // 目前限制Controller里面每个方法的RequiresPermissionsDesc注解是唯一的
                    // 如果允许相同，可能会造成内部权限不一致。
                    throw RuntimeException("权限已经存在，不能添加新权限")
                }

            }
            return root
        }

        fun listPermission(context: ApplicationContext, basicPackage: String): List<Permission> {
            val map = context.getBeansWithAnnotation(Controller::class.java)
            val permissions = ArrayList<Permission>()
            for (entry in map.entries) {
                val bean = entry.value
                if (!StringUtils.contains(ClassUtils.getPackageName(bean.javaClass), basicPackage)) {
                    continue
                }

                val clz = bean.javaClass
                val controllerClz = clz.superclass
                val clazzRequestMapping = AnnotationUtils.findAnnotation(controllerClz, RequestMapping::class.java)
                val methods = MethodUtils.getMethodsListWithAnnotation(controllerClz, RequiresPermissions::class.java)
                for (method in methods) {
                    val requiresPermissions = AnnotationUtils.getAnnotation(method, RequiresPermissions::class.java)
                    val requiresPermissionsDesc = AnnotationUtils.getAnnotation(method, RequiresPermissionsDesc::class.java)

                    if (requiresPermissions == null || requiresPermissionsDesc == null) {
                        continue
                    }

                    var api = ""
                    if (clazzRequestMapping != null) {
                        api = clazzRequestMapping!!.value[0]
                    }

                    val postMapping = AnnotationUtils.getAnnotation(method, PostMapping::class.java)
                    if (postMapping != null) {
                        api = "POST " + api + postMapping!!.value[0]

                        val permission = Permission()
                        permission.requiresPermissions = requiresPermissions
                        permission.requiresPermissionsDesc = requiresPermissionsDesc
                        permission.api = api
                        permissions.add(permission)
                        continue
                    }
                    val getMapping = AnnotationUtils.getAnnotation(method, GetMapping::class.java)
                    if (getMapping != null) {
                        api = "GET " + api + getMapping!!.value[0]
                        val permission = Permission()
                        permission.requiresPermissions = requiresPermissions
                        permission.requiresPermissionsDesc = requiresPermissionsDesc
                        permission.api = api
                        permissions.add(permission)
                        continue
                    }
                    // TODO
                    // 这里只支持GetMapping注解或者PostMapping注解，应该进一步提供灵活性
                    throw RuntimeException("目前权限管理应该在method的前面使用GetMapping注解或者PostMapping注解")
                }
            }
            return permissions
        }

        fun listPermissionString(permissions: List<Permission>): Set<String> {
            val permissionsString = HashSet<String>()
            for (permission in permissions) {
                permissionsString.add(permission.requiresPermissions!!.value[0])
            }
            return permissionsString
        }

    }
}