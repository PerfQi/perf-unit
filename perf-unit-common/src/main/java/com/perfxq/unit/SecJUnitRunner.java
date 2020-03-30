package com.perfxq.unit;



import com.perfxq.unit.datasource.dynamic.SecTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Import({SecTestConfiguration.class})
public class SecJUnitRunner extends AbstractJUnit4SpringContextTests implements SecTestContext{
    protected Map<String,Object> attachment = new HashMap<String,Object>();
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DataSourceProperties dataSourceProperties;

    private boolean inited = false;

    public synchronized void init(){
        if (inited){
            return;
        }
        doInit();
        inited = true;
    }

    private void doInit(){
        attachment.put("dataSource",dataSource);
        attachment.put("dataSourceProperties",dataSourceProperties);
    }

    public Map<String, Object> getContext() {
        return attachment;
    }
}
