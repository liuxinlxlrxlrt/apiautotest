package com.api.autotest.util;


import com.api.autotest.mapper.SqlMapperAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MybatisUtil {
    @Autowired
    private SqlMapperAnnotation sqlMapperAnnotation;

    public static MybatisUtil mybatisUtil;

    @PostConstruct
    public void init() {
        mybatisUtil = this;
        mybatisUtil.sqlMapperAnnotation = this.sqlMapperAnnotation;
    }


    public static Map<String, String> query(String sql) {

        Map<String, String> columnLabelAndValues = new LinkedHashMap<>();

        List<LinkedHashMap<String, Object>> linkedHashMaps = mybatisUtil.sqlMapperAnnotation.query(sql);

        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : linkedHashMaps) {
            Set<Map.Entry<String, Object>> entries = stringObjectLinkedHashMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                System.out.println(entry.getKey()+";"+entry.getValue());
                columnLabelAndValues.put(entry.getKey(),entry.getValue().toString());
            }
        }
        return columnLabelAndValues;
    }

    public static String oprateSqlList(String sqlList){
        int insert=0;
        String[] strs=sqlList.split(";");
        for (String str : strs) {

            if (str.contains("DELETE")){
                insert = mybatisUtil.sqlMapperAnnotation.delete(str);
            }else if (str.contains("UPATE")){
                insert = mybatisUtil.sqlMapperAnnotation.update(str);
            } else if (str.contains("INSERT")){
                insert = mybatisUtil.sqlMapperAnnotation.insert(str);
            }
            System.out.println(str);
        }
        return insert==1?"true":"false";
    }
}
