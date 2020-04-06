package com.perfxq.unit.datasource.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SecTestConfiguration {
    @Autowired
    DataSourceProperties dataSourceProperties;
    /**
     * 注册动态数据源
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        SecTestRoutingDataSource dynamicRoutingDataSource = new SecTestRoutingDataSource();
        DataSource dataSource =  dataSourceProperties.initializeDataSourceBuilder().type(DriverManagerDataSource.class).build();
        dynamicRoutingDataSource.setDefaultTargetDataSource(dataSource);// 设置默认数据源
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap();
        map.put("dataSource", dataSource);
        dynamicRoutingDataSource.setTargetDataSources(map);
        return dynamicRoutingDataSource;
    }
}
