package com.api.autotest.pojo;


import lombok.Data;

import java.util.Map;

/**
 * 数据库查询结果实体类
 */
@Data
public class DBQueryResult {
    /**
     * 脚本编号
     */
    private String no;
    /**
     * 脚本查到的数据，保存到map中，key的是字段名，value保存的是对应字段查到的数据
     */
    private Map<String,Object> columnLabelAndValues;
}
