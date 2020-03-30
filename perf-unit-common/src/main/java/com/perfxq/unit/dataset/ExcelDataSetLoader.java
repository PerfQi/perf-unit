package com.perfxq.unit.dataset;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.springframework.core.io.Resource;

import java.io.InputStream;

public class ExcelDataSetLoader extends AbstractDataSetLoader{
    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        InputStream inputStream = resource.getInputStream();
        try {
            return new XlsDataSet(inputStream);
        } finally {
            inputStream.close();
        }
    }
}
