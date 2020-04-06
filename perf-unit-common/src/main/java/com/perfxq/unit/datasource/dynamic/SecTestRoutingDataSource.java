package com.perfxq.unit.datasource.dynamic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecTestRoutingDataSource extends AbstractRoutingDataSource {
    private static final Log log = LogFactory.getLog(SecTestRoutingDataSource.class);

    private static ConcurrentHashMap<Object, Object> targetDataSources = new ConcurrentHashMap<Object, Object>();
    protected Object determineCurrentLookupKey() {
        log.info("the thread name is:"+ Thread.currentThread().getName()+",current DataSource is:"+ SecDataSourceContextHolder.getDataSourceKey());
        return SecDataSourceContextHolder.getDataSourceKey();
    }

    public synchronized boolean addDataSource(String dbName,DataSource dataSource) {
        try {
            String database = dbName;//获取要添加的数据库名
            if (database==null||database.equals(""))
                return false;
            if (SecTestRoutingDataSource.isExistDataSource(database)){
                return true;
            }
            ConcurrentHashMap<Object, Object> targetMap = SecTestRoutingDataSource.targetDataSources;
            targetMap.put(dbName, dataSource);
            this.setTargetDataSources(targetMap);
            this.afterPropertiesSet();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }

    /**
     * 动态增加数据源
     * @param
     * @return
     */
    public synchronized boolean addDataSource(String dbName,String url,String userName,String password) {
        try {
            String database = dbName;//获取要添加的数据库名
            if (database==null||database.equals(""))
                return false;
            if (SecTestRoutingDataSource.isExistDataSource(database)){
                return true;
            }
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl(url);
            dataSource.setUsername(userName);
            dataSource.setPassword(password);

            Map<Object, Object> targetMap = SecTestRoutingDataSource.targetDataSources;
            targetMap.put(database, dataSource);
            this.afterPropertiesSet();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        SecTestRoutingDataSource.targetDataSources = (ConcurrentHashMap)targetDataSources;
    }

    /**
     * 是否存在当前key的 DataSource
     *
     * @param key
     * @return 存在返回 true, 不存在返回 false
     */
    public static boolean isExistDataSource(String key) {
        return targetDataSources.containsKey(key);
    }

}
