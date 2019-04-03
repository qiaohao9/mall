package me.vastpeng.mall.admin.shiro

import org.apache.commons.lang3.StringUtils
import org.apache.shiro.web.servlet.ShiroHttpServletRequest
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager
import org.apache.shiro.web.util.WebUtils
import java.io.Serializable
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse


class AdminWebSessionManager : DefaultWebSessionManager() {
    companion object {
        const val LOGIN_TOKEN_KEY = "X-Litemall-Admin-Token"
        private const val REFERENCED_SESSION_ID_SOURCE = "Stateless request"
    }

    override fun getSessionId(request: ServletRequest, response: ServletResponse): Serializable {
        val id = WebUtils.toHttp(request).getHeader(LOGIN_TOKEN_KEY)
        if (!StringUtils.isEmpty(id)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE)
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id)
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, java.lang.Boolean.TRUE)
            return id
        } else {
            return super.getSessionId(request, response)
        }
    }


}