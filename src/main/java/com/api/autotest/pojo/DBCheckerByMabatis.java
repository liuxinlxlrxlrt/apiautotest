package com.api.autotest.pojo;

import lombok.Data;

@Data
public class DBCheckerByMabatis {
    private String tableName;
    private String sql;

    @Override
    public String toString() {
        return "tableName=" + tableName + ",sql=" + sql;
    }
}
