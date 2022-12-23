package com.api.autotest.pojo;


import lombok.Data;

import java.util.Map;

/**
 * 数据库查询结果实体类
 */
@Data
public class DBQueryByMabtisResult {
    /**
     * 脚本表名
     */
    private String tableName;
    /**
     * 脚本查到的数据，保存到map中，key的是字段名，value保存的是对应字段查到的数据
     */
    private Map<String, String> columnLabelAndValues;
}
