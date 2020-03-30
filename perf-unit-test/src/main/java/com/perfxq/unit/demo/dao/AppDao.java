package com.perfxq.unit.demo.dao;

import com.perfxq.unit.demo.entity.App;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppDao {

    List<App> queryAppInfo(Long id);

    int removeAppInfo(Long id);
}
