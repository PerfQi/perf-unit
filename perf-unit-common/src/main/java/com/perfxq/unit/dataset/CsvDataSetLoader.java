package com.perfxq.unit.dataset;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.springframework.core.io.Resource;

import java.io.File;

public class CsvDataSetLoader extends AbstractDataSetLoader{
    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        File file = resource.getFile();
        if (file.exists()){
            return new CsvDataSet(file);
        }else {
            throw new Exception("the file is not exist");
        }
    }
}
