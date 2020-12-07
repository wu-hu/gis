package com.jh.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jh.domain.SysUser;
import com.jh.shiro.ShiroUtils;
import com.jh.utils.CommonUtils;
import com.jh.utils.ajax.AjaxResult;
import com.jh.utils.string.StringUtils;
import com.jh.web.domain.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return AjaxResult.error("请求方式错误！");
    }

    @PostMapping("/login")
    public String doLogin(@RequestBody JSONObject jsonParam) {
//        Subject subject = ShiroUtils.getSubject();
//        UsernamePasswordToken token = new UsernamePasswordToken("admin", "", false);
//        subject.login(token);
//        PrincipalCollection principalCollection = subject.getPrincipals();
//        String realmName = principalCollection.getRealmNames().iterator().next();
//        return "login";
        // 参数检查
        if (StringUtils.isEmpty(jsonParam.get("username").toString())
                || StringUtils.isEmpty(jsonParam.get("password").toString())) {
            return AjaxResult.error("参数错误！");
        }

        SysUser user = ShiroUtils.getSysUser();
        if (CommonUtils.isNotNull(user)) {
            //尝试SSO登录，如果登录成功，则判断新旧用户是否相同，如果不同，则删除旧用户信息，重新登录
            //如果相同，直接返回成功
            /**
             * TBD  SSO登录
             */
            String sso_url = "";
            try
            {
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(sso_url, String.class);
                log.debug(sso_url);
                log.debug(result);
                UserModel userModel = JSON.parseObject(result, UserModel.class);
                if (CommonUtils.isNull(userModel) || CommonUtils.isNotNull(userModel.getHasError())) {
                    return AjaxResult.error("登录失败！");
                }
            } catch (RestClientException e) {
                return AjaxResult.error("系统异常，请稍后重试！", 2);
            }

            // 删除现有登录信息
            ShiroUtils.logout();
            //
            if (true) {
                // 删除现有登录信息
                ShiroUtils.logout();
            } else {
                return AjaxResult.success();
            }
        }

        return jsonParam.toJSONString();
    }

    @GetMapping("/unauthorized")
    public String unauth() {
        return AjaxResult.error("权限不足！");
    }
}
