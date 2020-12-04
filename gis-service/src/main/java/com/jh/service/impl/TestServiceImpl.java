package com.jh.service.impl;

import com.jh.annotation.DataSource;
import com.jh.datasource.DataSourceType;
import com.jh.domain.Test;
import com.jh.mapper.TestMapper;
import com.jh.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DataSource(value = DataSourceType.SLAVE)
public class TestServiceImpl implements ITestService {
    @Autowired
    private TestMapper testMapper;

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public Test selectById(Integer id){
        return testMapper.selectByPrimaryKey(id);
    }
}
