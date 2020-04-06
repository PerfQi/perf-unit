package com.perfxq.unit;
import com.perfxq.unit.annotation.SecTest;
import com.perfxq.unit.bean.DatabaseDataSourceConnectionFactoryBean;
import com.perfxq.unit.datasource.dynamic.SecDataSourceContextHolder;
import com.perfxq.unit.datasource.dynamic.SecTestRoutingDataSource;
import com.perfxq.unit.runner.SecParallelSpringRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.IDatabaseConnection;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SecJunitMethodListener extends AbstractTestExecutionListener {
    private static final Log log = LogFactory.getLog(SecJunitMethodListener.class);

    private static AtomicLong counter = new AtomicLong(0);

    /**
     * prepareTestInstance 拿不到method的注解信息
     */
    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        Object classInstance = testContext.getTestInstance();
        if (!(classInstance instanceof SecJUnitRunner)) {
            return;
        }
        if (!(classInstance instanceof SecTestContext)) {
            return;
        }

        SecJUnitRunner runner = (SecJUnitRunner) classInstance;
        runner.init();
    }

    private void prepareMethodDatabase(TestContext testContext,DataSourceProperties dataSourceProperties,SecTestRoutingDataSource sysDefaultDataSource)
            throws Exception {
        Method jdkMethod = testContext.getTestMethod();
        /**
         * 生成新的数据源
         */
        int spliIndex = dataSourceProperties.getUrl().indexOf(";");
        String url = "";
        Long curId = counter.getAndIncrement();
        if (spliIndex != -1){
            url = dataSourceProperties.getUrl().substring(0,spliIndex);
            url = url +  "_" + jdkMethod.getName()+ "_" + curId;
            url = url + dataSourceProperties.getUrl().substring(spliIndex,dataSourceProperties.getUrl().length());
        }else {
            url = dataSourceProperties.getUrl()+ "_" + jdkMethod.getName() + "_" + curId;
        }

        DataSource newDataSource =  dataSourceProperties.initializeDataSourceBuilder().url(url).type(DriverManagerDataSource.class).build();
        sysDefaultDataSource.addDataSource(jdkMethod.getName()+ "_" + curId,newDataSource);
        SecDataSourceContextHolder.setDataSourceKey(jdkMethod.getName()+ "_" + curId);

        /**
         * sql文件创建表操作
         */
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(newDataSource);
        ClassRelativeResourceLoader classRelativeResourceLoader = new ClassRelativeResourceLoader(this.getClass());

        Resource[] resourceList = new Resource[dataSourceProperties.getSchema().size()];
        for (int i=0;i<dataSourceProperties.getSchema().size();i++){
            Resource path = classRelativeResourceLoader.getResource(dataSourceProperties.getSchema().get(i));
            resourceList[i] = path;
        }
        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(false,true,"utf-8",  resourceList));
        dataSourceInitializer.setEnabled(true);
        dataSourceInitializer.afterPropertiesSet();
    }

    private void prepareClassDatabase(TestContext testContext,DataSourceProperties dataSourceProperties,SecTestRoutingDataSource sysDefaultDataSource)
            throws Exception {
        Class testClass = testContext.getTestClass();
        /**
         * 生成新的数据源
         */
        int spliIndex = dataSourceProperties.getUrl().indexOf(";");
        String url = "";
        if (spliIndex != -1){
            url = dataSourceProperties.getUrl().substring(0,spliIndex);
            url = url +  "_" + testClass.getName() + "_" + testClass.hashCode();
            url = url + dataSourceProperties.getUrl().substring(spliIndex,dataSourceProperties.getUrl().length());
        }else {
            url = dataSourceProperties.getUrl()+ "_" + testClass.getName()  + "_" + testClass.hashCode();
        }

        if (sysDefaultDataSource.isExistDataSource(url)){
            log.warn("the datasource url is exist,url:" +url);
            return;
        }
        DataSource newDataSource =  dataSourceProperties.initializeDataSourceBuilder().url(url).type(DriverManagerDataSource.class).build();
        sysDefaultDataSource.addDataSource(testClass.getName()+ "_" + testClass.hashCode(),newDataSource);
        SecDataSourceContextHolder.setDataSourceKey(testClass.getName()+ "_" + testClass.hashCode());

        /**
         * sql文件创建表操作
         */
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(newDataSource);
        ClassRelativeResourceLoader classRelativeResourceLoader = new ClassRelativeResourceLoader(this.getClass());

        Resource[] resourceList = new Resource[dataSourceProperties.getSchema().size()];
        for (int i=0;i<dataSourceProperties.getSchema().size();i++){
            Resource path = classRelativeResourceLoader.getResource(dataSourceProperties.getSchema().get(i));
            resourceList[i] = path;
        }
        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(false,true,"utf-8",  resourceList));
        dataSourceInitializer.setEnabled(true);
        dataSourceInitializer.afterPropertiesSet();
    }

    private void prepareDatabaseConnection(TestContext testContext,DataSource dataSource)
            throws Exception {
        IDatabaseConnection[] connections = new IDatabaseConnection[1];
        Object databaseConnection = DatabaseDataSourceConnectionFactoryBean
                    .newConnection(dataSource);
        Assert.isInstanceOf(IDatabaseConnection.class, databaseConnection);
        connections[0] = (IDatabaseConnection) databaseConnection;
        ThreadLocalUtil.set(SecInfo.CONNECTION_ATTRIBUTE, new DatabaseConnections( new String[] {"dataSource"}, connections));
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        Method jdkMethod = testContext.getTestMethod();
        if (jdkMethod == null) {
            return;
        }

        Object classInstance = testContext.getTestInstance();
        if (!(classInstance instanceof SecJUnitRunner)) {
            return;
        }

        SecTest secTest = jdkMethod.getAnnotation(SecTest.class);
        if (secTest == null) {
            return;
        }

        SecTestContext secTestContext = (SecTestContext) classInstance;
        Map<String, Object> stringObjectMap = secTestContext.getContext();

        SecTestRoutingDataSource sysDefaultDataSource = (SecTestRoutingDataSource)stringObjectMap.get("dataSource");
        ThreadLocalUtil.set(SecInfo.DATASOURCE_ATTRIBUTE,  sysDefaultDataSource);
        DataSourceProperties dataSourceProperties = (DataSourceProperties) stringObjectMap.get("dataSourceProperties");
        Class testClass = testContext.getTestClass();
//        int nParallFlag = 0 ;
//        RunWith runWithAnotation = (RunWith) testClass.getAnnotation(RunWith.class);
//        if (runWithAnotation != null){
//            Class<? extends Runner> runAno = runWithAnotation.value();
//            if (runAno == SecParallelSpringRunner.class){
//                nParallFlag = 1;
//            }
//        }
//        if (nParallFlag == 1){
            prepareMethodDatabase(testContext,dataSourceProperties,sysDefaultDataSource);
//        }else {
//            prepareClassDatabase(testContext,dataSourceProperties,sysDefaultDataSource);
//        }
        prepareDatabaseConnection(testContext,sysDefaultDataSource);

        SecJUnitRunner runner = (SecJUnitRunner) classInstance;
        if (secTest.enablePrepare()) {
            SecTestRunnerTool.prepare(secTest, runner ,testContext);
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        boolean hasException = (testContext.getTestException() != null) ? true : false;

        Method jdkMethod = testContext.getTestMethod();
        if (jdkMethod == null) {
            return;
        }

        Object classInstance = testContext.getTestInstance();
        if (!(classInstance instanceof SecJUnitRunner)) {
            return;
        }

        SecTest secTest = jdkMethod.getAnnotation(SecTest.class);
        if (secTest == null) {
            return;
        }

        SecJUnitRunner runner = (SecJUnitRunner) classInstance;
        if (!hasException && secTest.enableCheck()) {
            SecTestRunnerTool.check(secTest, runner ,testContext);
        }

        if (secTest.enablePrepare()) {
            //清理数据
            //SecTestRunnerTool.clean(secTest, runner ,testContext);
        }
    }
}

