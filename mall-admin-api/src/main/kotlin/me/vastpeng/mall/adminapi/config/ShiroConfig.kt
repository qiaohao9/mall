package me.vastpeng.mall.adminapi.config

import me.vastpeng.mall.adminapi.shiro.AdminAuthorizingRealm
import org.apache.shiro.realm.Realm
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.LinkedHashMap
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import me.vastpeng.mall.adminapi.shiro.AdminWebSessionManager
import org.apache.shiro.session.mgt.SessionManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.context.annotation.DependsOn


@Configuration
class ShiroConfig {
    @Bean
    fun realm(): Realm {
        return AdminAuthorizingRealm()
    }

    @Bean
    fun shirFilter(securityManager: SecurityManager): ShiroFilterFactoryBean {
        val shiroFilterFactoryBean = ShiroFilterFactoryBean()
        shiroFilterFactoryBean.securityManager = securityManager
        val filterChainDefinitionMap = LinkedHashMap<String, String>()
        filterChainDefinitionMap["/admin/auth/login"] = "anon"
        filterChainDefinitionMap["/admin/auth/401"] = "anon"
        filterChainDefinitionMap["/admin/auth/index"] = "anon"
        filterChainDefinitionMap["/admin/auth/403"] = "anon"

        filterChainDefinitionMap["/admin/**"] = "authc"
        shiroFilterFactoryBean.loginUrl = "/admin/auth/401"
        shiroFilterFactoryBean.successUrl = "/admin/auth/index"
        shiroFilterFactoryBean.unauthorizedUrl = "/admin/auth/403"
        shiroFilterFactoryBean.filterChainDefinitionMap = filterChainDefinitionMap
        return shiroFilterFactoryBean
    }

    @Bean
    fun sessionManager(): SessionManager {
        return AdminWebSessionManager()
    }

    @Bean
    fun securityManager(): DefaultWebSecurityManager {
        val securityManager = DefaultWebSecurityManager()
        securityManager.setRealm(realm())
        securityManager.sessionManager = sessionManager()
        return securityManager
    }

    @Bean
    fun authorizationAttributeSourceAdvisor(securityManager: SecurityManager): AuthorizationAttributeSourceAdvisor {
        val authorizationAttributeSourceAdvisor = AuthorizationAttributeSourceAdvisor()
        authorizationAttributeSourceAdvisor.securityManager = securityManager
        return authorizationAttributeSourceAdvisor
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    fun defaultAdvisorAutoProxyCreator(): DefaultAdvisorAutoProxyCreator {
        val creator = DefaultAdvisorAutoProxyCreator()
        creator.isProxyTargetClass = true
        return creator
    }
}