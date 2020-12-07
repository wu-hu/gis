package com.jh.shiro.service;

import com.jh.domain.SysUser;
import com.jh.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroLoginService {
    @Autowired
    private SysUserMapper userMapper;

    public SysUser login(String userName, String password) {
        SysUser user = userMapper.selectByPrimaryKey(1L);
        return user;
    }
}
