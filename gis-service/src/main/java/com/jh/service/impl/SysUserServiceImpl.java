package com.jh.service.impl;

import com.jh.domain.SysUser;
import com.jh.mapper.SysUserMapper;
import com.jh.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper userMapper;

    @Override
    public SysUser selectUserByUserId(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }
}
