package com.jh.shiro.realm;

import com.jh.domain.SysUser;
import com.jh.shiro.ShiroUtils;
import com.jh.shiro.service.ShiroLoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义Realm 处理登录 权限
 */
@Slf4j
public class UserRealm extends AuthorizingRealm
{
//    @Autowired
//    private ISysMenuService menuService;
//
//    @Autowired
//    private ISysRoleService roleService;

    @Autowired
    private ShiroLoginService loginService;

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0)
    {
        SysUser user = ShiroUtils.getSysUser();
        // 角色列表
        Set<String> roles = new HashSet<String>();
        // 功能列表
        Set<String> menus = new HashSet<String>();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            info.addRole("admin");
            info.addStringPermission("*:*:*");
        }
        else
        {
            // 角色加入AuthorizationInfo认证对象
            info.setRoles(roles);
            // 权限加入AuthorizationInfo认证对象
            info.setStringPermissions(menus);
        }
        return info;
    }

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException
    {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = "";
        if (upToken.getPassword() != null)
        {
            password = new String(upToken.getPassword());
        }

        SysUser user = null;
        try
        {
            user = loginService.login(username, password);
        }
        catch (Exception e)
        {
            log.info("对用户[" + username + "]进行登录验证..验证未通过{}", e.getMessage());
            throw new AuthenticationException(e.getMessage(), e);
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
        Session session = ShiroUtils.getSession();
        session.setAttribute("username", user.getUserName());
        return info;
    }

    /**
     * 清理指定用户授权信息缓存
     */
    public void clearCachedAuthorizationInfo(Object principal)
    {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
        this.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 清理所有用户授权信息缓存
     */
    public void clearAllCachedAuthorizationInfo()
    {
        Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null)
        {
            for (Object key : cache.keys())
            {
                cache.remove(key);
            }
        }
    }
}