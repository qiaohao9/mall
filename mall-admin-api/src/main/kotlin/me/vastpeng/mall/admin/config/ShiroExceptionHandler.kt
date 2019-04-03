package me.vastpeng.mall.admin.config

import me.vastpeng.mall.core.util.ResponseUtil
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authz.AuthorizationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody


@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
class ShiroExceptionHandler {

    @ExceptionHandler(AuthenticationException::class)
    @ResponseBody
    fun unauthenticatedHandler(e: AuthenticationException): Any {
        e.printStackTrace()
        return ResponseUtil.unlogin()
    }

    @ExceptionHandler(AuthorizationException::class)
    @ResponseBody
    fun unauthorizedHandler(e: AuthorizationException): Any {
        e.printStackTrace()
        return ResponseUtil.unauthz()
    }

}