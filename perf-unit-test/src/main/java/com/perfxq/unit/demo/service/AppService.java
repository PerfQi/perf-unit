package com.perfxq.unit.demo.service;

import com.perfxq.unit.demo.dao.AppDao;
import com.perfxq.unit.demo.entity.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppService {
    @Autowired
    private AppDao appDao;

    public List<App> queryAppInfo(Long id){
        return appDao.queryAppInfo(id);
    }

    public int removeAppInfo(Long id){
        return appDao.removeAppInfo(id);
    }

}
