package com.jh.service;

import com.jh.domain.SysUser;

public interface ISysUserService {
    public SysUser selectUserByUserId(Long userId);
}
