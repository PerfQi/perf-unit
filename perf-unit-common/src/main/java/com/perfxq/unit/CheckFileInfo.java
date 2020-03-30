package com.perfxq.unit;

import java.util.List;
import java.util.Map;

public class CheckFileInfo {
//    "checkType": "DB_CHECK",
//            "checkDesc": "检查 更新结果正确性",
//            "checkSqlQuery": "select status from user where user_id=1",
//            "checkExpectedData": [
//    {
//        "status": 1
//    }
//         ]
//}
    private String checkType;
    private String checkDesc;
    private String checkSqlQuery;
    private List<Map<String,Object>> checkExpectedData;

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckDesc() {
        return checkDesc;
    }

    public void setCheckDesc(String checkDesc) {
        this.checkDesc = checkDesc;
    }

    public String getCheckSqlQuery() {
        return checkSqlQuery;
    }

    public void setCheckSqlQuery(String checkSqlQuery) {
        this.checkSqlQuery = checkSqlQuery;
    }

    public List<Map<String, Object>> getCheckExpectedData() {
        return checkExpectedData;
    }

    public void setCheckExpectedData(List<Map<String, Object>> checkExpectedData) {
        this.checkExpectedData = checkExpectedData;
    }


}
