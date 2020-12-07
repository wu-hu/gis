package com.jh.web.controller;

import com.jh.domain.SysUser;
import com.jh.domain.Test;
import com.jh.service.ISysUserService;
import com.jh.service.ITestService;
import com.jh.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ITestService testService;

    @GetMapping("/")
    public String index(){
        return "Hello World!";
    }

    @GetMapping("/getuser/{id}")
    public String getUser(@PathVariable(name = "id") String id) {
        SysUser user = userService.selectUserByUserId(Long.parseLong(id));
        return user.toString();
    }

    @GetMapping("/slave")
    @RequiresPermissions("a:b:c")
    public String slave() {
        Session session = ShiroUtils.getSession();
        Test test = testService.selectById(1);
        return test.toString();
    }


}
