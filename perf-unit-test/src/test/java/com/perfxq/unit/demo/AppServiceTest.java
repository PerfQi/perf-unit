package com.perfxq.unit.demo;

import com.perfxq.unit.SecJUnitRunner;
import com.perfxq.unit.SecJunitMethodListener;
import com.perfxq.unit.annotation.PrepareDataType;
import com.perfxq.unit.annotation.SecTest;
import com.perfxq.unit.runner.SecParallelSpringRunner;
import com.perfxq.unit.demo.entity.App;
import com.perfxq.unit.demo.service.AppService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

@RunWith(SecParallelSpringRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, SecJunitMethodListener.class })
public class AppServiceTest extends SecJUnitRunner {
    @Autowired
    private AppService appService;

    @Test
    @SecTest(enablePrepare=true,prepareDateType = PrepareDataType.EXCEL2DB,prepareDateConfig = {"App.xlsx"})
    public void testQueryAppInfo() {
        List<App> list =  appService.queryAppInfo(0l);
        Assert.assertEquals(1, list.size());
    }

    @Test
    @SecTest(enablePrepare=true,prepareDateType = PrepareDataType.EXCEL2DB,prepareDateConfig = {"App.xlsx"})
    public void testRemoveAppInfo() {
        int ret =  appService.removeAppInfo(1l);
        Assert.assertTrue(ret>0);
    }

}