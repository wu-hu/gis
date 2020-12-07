package com.jh.config;

import com.jh.domain.SysUser;
import com.jh.shiro.realm.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {
//    @Value("shiro.cookie.cookieName")
//    private String cookieName;
//
//    @Value("shiro.session.timeout")
//    private long timeout;

    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        return redisManager;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    @Bean
    public RedisSessionDAO sessionDAO(){
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setRedisManager(redisManager());
        sessionDAO.setSessionIdGenerator(sessionIdGenerator()); //  Session ID 生成器
        return sessionDAO;
    }

    @Bean
    public SimpleCookie cookie(){
        SimpleCookie cookie = new SimpleCookie("cookiename");
        cookie.setHttpOnly(true);
        cookie.setPath("/");        //  path为 / 用于多个系统共享JSESSIONID
        return cookie;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(1800000L);    // 设置session超时
        sessionManager.setDeleteInvalidSessions(true);      // 删除无效session
        sessionManager.setSessionIdCookie(cookie());        // 设置JSESSIONID
        sessionManager.setSessionDAO(sessionDAO());         // 设置sessionDAO
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 1. 配置SecurityManager
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());  // 设置realm
        securityManager.setSessionManager(sessionManager());    // 设置sessionManager
        securityManager.setCacheManager(redisCacheManager()); // 配置缓存的话，退出登录的时候crazycake会报错，要求放在session里面的实体类必须有个id标识
        return securityManager;
    }

    @Bean
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager cacheManager = new RedisCacheManager();   // crazycake 实现
        cacheManager.setRedisManager(redisManager());
        cacheManager.setPrincipalIdFieldName("userId");
        return cacheManager;
    }


    /**
     * 3. 配置Realm
     * @return
     */
    @Bean
    public UserRealm userRealm(){
        UserRealm realm = new UserRealm();
        return realm;
    }


    /**
     * 4. 配置LifecycleBeanPostProcessor，可以来自动的调用配置在Spring IOC容器中 Shiro Bean 的生命周期方法
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 5. 启用IOC容器中使用Shiro的注解，但是必须配置第四步才可以使用
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setUsePrefix(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 6. 配置ShiroFilter
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        // 公共路径
        map.put("/login", "anon");
        map.put("/error", "anon");

        // 登出,项目中没有/logout路径,因为shiro是过滤器,而SpringMVC是Servlet,Shiro会先执行
        map.put("/logout", "logout");

        // everything else requires authentication:
        map.put("/**", "authc");

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 配置SecurityManager
        factoryBean.setSecurityManager(securityManager());
        // 配置权限路径
        factoryBean.setFilterChainDefinitionMap(map);
        // 配置登录url
        factoryBean.setLoginUrl("/login");
        // 配置无权限路径
        factoryBean.setUnauthorizedUrl("/unauthorized");

        return factoryBean;
    }

    /**
     * 开启Shiro注解通知器
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            @Qualifier("securityManager") SecurityManager securityManager)
    {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 配置RedisTemplate，充当数据库服务
     * @return
     */
    @Bean
    public RedisTemplate<String,SysUser> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, SysUser> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<SysUser>(SysUser.class));
        return redisTemplate;
    }
}
