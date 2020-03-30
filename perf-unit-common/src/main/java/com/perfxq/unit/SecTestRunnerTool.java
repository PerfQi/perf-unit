package com.perfxq.unit;

import com.alibaba.fastjson.JSONObject;
import com.perfxq.unit.annotation.PrepareDataType;
import com.perfxq.unit.annotation.SecTest;
import com.perfxq.unit.dataset.*;
import com.perfxq.unit.dataset.builder.DataRowBuilder;
import com.perfxq.unit.dataset.builder.DataSetBuilder;
import com.perfxq.unit.datasource.dynamic.SecDataSourceContextHolder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class SecTestRunnerTool {
    private static final Log logger = LogFactory.getLog(SecTestRunnerTool.class);

    public static void prepare(SecTest secTest, SecJUnitRunner runner, TestContext testContext) throws Exception {
        Class<? extends DataSetLoader> dataSetLoaderClass = FlatXmlDataSetLoader.class;
        PrepareDataType prepareDataType = secTest.prepareDateType();
        if (prepareDataType.equals(PrepareDataType.XML2DB)){
            dataSetLoaderClass = FlatXmlDataSetLoader.class;
        }else if (prepareDataType.equals(PrepareDataType.CSV2DB)){
            dataSetLoaderClass = CsvDataSetLoader.class;
        }else if (prepareDataType.equals(PrepareDataType.EXCEL2DB)){
            dataSetLoaderClass = ExcelDataSetLoader.class;
        }
        try {
            ThreadLocalUtil.set(SecInfo.DATA_SET_LOADER_ATTRIBUTE, dataSetLoaderClass.newInstance());
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Unable to create data set loader instance for " + dataSetLoaderClass, ex);
        }

        DatabaseConnections connections = ThreadLocalUtil.get(SecInfo.CONNECTION_ATTRIBUTE);
        List<IDataSet> datasets = loadDataSets(testContext, secTest);
        org.dbunit.operation.DatabaseOperation dbUnitOperation = org.dbunit.operation.DatabaseOperation.CLEAN_INSERT;
        if (!datasets.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing loadDataSets of @DatabaseTest using CLEAN_INSERT on " + datasets.toString());
            }
            IDatabaseConnection connection = connections.getDefault();
            IDataSet dataSet = new CompositeDataSet(datasets.toArray(new IDataSet[datasets.size()]));

            logger.info("The thread info: "
                    + Thread.currentThread().getName() + ",DataSource info key:" +  SecDataSourceContextHolder.getDataSourceKey()
                    + ",the table name:" + StringUtils.arrayToCommaDelimitedString(dataSet.getTableNames()));
            ThreadLocalUtil.set(SecInfo.DATA_SET_ATTRIBUTE, dataSet);
            try {
                dbUnitOperation.execute(connection, dataSet);
            } catch (JdbcSQLIntegrityConstraintViolationException e){

                logger.error("The sql exception,the sql: " + e.getSQL()
                 + ",the orgmessage:" + e.getOriginalMessage() + ",the msg:"+ e.getMessage(),e);
            }
            catch (DatabaseUnitException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            logger.info("The thread info: "
                    + Thread.currentThread().getName() + " end" );
        }
    }

    public static void check(SecTest secTest, SecJUnitRunner runner, TestContext testContext) throws Exception {
        if (testContext.getTestException() != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping @DatabaseTest expectation due to test exception "
                        + testContext.getTestException().getClass());
            }
            return;
        }

        DataSource dataSource = ThreadLocalUtil.get(SecInfo.DATASOURCE_ATTRIBUTE);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String[] checkFiles = secTest.checkConfigFiles();
        for (String file:checkFiles){
            ClassRelativeResourceLoader classRelativeResourceLoader = new ClassRelativeResourceLoader(runner.getClass());
            Resource resource = classRelativeResourceLoader.getResource(file);
            File resourceFile = resource.getFile();
            String jsonString = FileUtils.readFileToString(resourceFile);
            CheckFileInfo checkFileInfo = JSONObject.parseObject(jsonString,CheckFileInfo.class);

            List<Map<String, Object>> expectedData =  checkFileInfo.getCheckExpectedData();
            List<Map<String, Object>> result = jdbcTemplate.queryForList(checkFileInfo.getCheckSqlQuery());
            if (expectedData.size() != result.size()){
                throw new Exception();
            }

            DataSetBuilder expectedDataBuilder = new DataSetBuilder();
            for (Map<String, Object> rows:expectedData){
                DataRowBuilder rowBuilder = expectedDataBuilder.newRow("CheckResultDataTb");
                for (String colum : rows.keySet()) {
                    rowBuilder.with(colum,rows.get(colum));
                }
                rowBuilder.add();
            }
            IDataSet expectedDataSet = expectedDataBuilder.build();


            DataSetBuilder checkResultDataBuilder = new DataSetBuilder();
            for (Map<String, Object> rows:result){
                DataRowBuilder rowBuilder = checkResultDataBuilder.newRow("CheckResultDataTb");
                for (String colum : rows.keySet()) {
                    rowBuilder.with(colum,rows.get(colum));
                }
                rowBuilder.add();
            }
            IDataSet checkResultDataSet = checkResultDataBuilder.build();
            Assertion.assertEquals(expectedDataSet, checkResultDataSet);
        }
        /**
         *
         * {
         "checkType": "DB_CHECK",
         "checkDesc": "检查 更新结果正确性",
         "checkSqlQuery": "select status from user where user_id=1",
         "checkExpectedData": [
         {
         "status": 1
         }
         ]
         }
         */
//        DataSetModifier modifier = getModifier(testContext, annotations);
//        boolean override = false;
//        for (ExpectedDatabase annotation : annotations.getMethodAnnotations()) {
//            verifyExpected(testContext, connections, modifier, annotation);
//            override |= annotation.override();
//        }
//        if (!override) {
//            for (ExpectedDatabase annotation : annotations.getClassAnnotations()) {
//                verifyExpected(testContext, connections, modifier, annotation);
//            }
//        }
    }

//    private DataSetModifier getModifier(DbUnitTestContext testContext, DbUnitRunner.Annotations<ExpectedDatabase> annotations) {
//        DataSetModifiers modifiers = new DataSetModifiers();
//        for (ExpectedDatabase annotation : annotations) {
//            for (Class<? extends DataSetModifier> modifierClass : annotation.modifiers()) {
//                modifiers.add(testContext.getTestInstance(), modifierClass);
//            }
//        }
//        return modifiers;
//    }
    public static void clean(SecTest secTest, SecJUnitRunner runner, TestContext testContext) throws DatabaseUnitException, SQLException {
        DatabaseConnections connections = ThreadLocalUtil.get(SecInfo.CONNECTION_ATTRIBUTE);
        IDatabaseConnection connection = connections.getDefault();

        IDataSet dataSet = ThreadLocalUtil.get(SecInfo.DATA_SET_ATTRIBUTE);
        org.dbunit.operation.DatabaseOperation dbUnitOperation = org.dbunit.operation.DatabaseOperation.DELETE_ALL;
        dbUnitOperation.execute(connection, dataSet);
    }

    private static List<IDataSet> loadDataSets(TestContext testContext, SecTest secTest)
            throws Exception {
        List<IDataSet> datasets = new ArrayList<IDataSet>();
        for (String dataSetLocation : secTest.prepareDateConfig()) {
            datasets.add(loadDataset(testContext, dataSetLocation, DataSetModifier.NONE));
        }

        if (!datasets.isEmpty()) {
            return datasets;
        }

        if (secTest.prepareDateType().equals(PrepareDataType.CSV2DB)){
            DataSetLoader dataSetLoader = ThreadLocalUtil.get(SecInfo.DATA_SET_LOADER_ATTRIBUTE);
            IDataSet dataSet = dataSetLoader.loadDataSet(testContext.getTestClass(), "");
            datasets.add(dataSet);
        }else {
            datasets.add(getFullDatabaseDataSet());
        }
        return datasets;
    }

    private static IDataSet getFullDatabaseDataSet() throws Exception {
        DatabaseConnections connections = (DatabaseConnections)ThreadLocalUtil.get(SecInfo.CONNECTION_ATTRIBUTE);
        IDatabaseConnection connection = connections.getDefault();
        return connection.createDataSet();
    }

    private static IDataSet loadDataset(TestContext testContext, String dataSetLocation, DataSetModifier modifier)
            throws Exception {
        DataSetLoader dataSetLoader = ThreadLocalUtil.get(SecInfo.DATA_SET_LOADER_ATTRIBUTE);
        if (StringUtils.hasLength(dataSetLocation)) {
            IDataSet dataSet = dataSetLoader.loadDataSet(testContext.getTestClass(), dataSetLocation);
            dataSet = modifier.modify(dataSet);
            Assert.notNull(dataSet,
                    "Unable to load dataset from \"" + dataSetLocation + "\" using " + dataSetLoader.getClass());
            return dataSet;
        }
        return null;
    }
}
