package com.api.autotest.util;

import com.alibaba.fastjson.JSONObject;
import com.api.autotest.pojo.DBCheckerByMabatis;
import com.api.autotest.pojo.DBQueryByMabtisResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBCheckByMybatisUtil {
    /**
     * 根据脚本执行查询并返回查询结果
     *
     * @param verifySql
     * @return
     */
    public static String doQuery(String verifySql) {
        List<DBQueryByMabtisResult> dbCheckByMybatisResults = new ArrayList();
        //JSONObject.parseArray(verifySql, DBCheckerByMabatis.class)将脚本字符串封装成了list对象
        List<DBCheckerByMabatis> dbCheckerByMabatis = JSONObject.parseArray(verifySql, DBCheckerByMabatis.class);
        //循环遍历，取出sql执行
        for (DBCheckerByMabatis dbCheckerByMabati : dbCheckerByMabatis) {
            String tableName = dbCheckerByMabati.getTableName();
            System.out.println("tableName：" + tableName);
            String sql = dbCheckerByMabati.getSql();
            System.out.println("sql：" + sql);
            //执行查询，获取结果
            Map<String, String> columnLabelAndValues = MybatisUtil.query(sql);
            Set<Map.Entry<String, String>> entries = columnLabelAndValues.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                System.out.print("key:"+entry.getKey()+",value:"+entry.getValue());
            }
            //将结果放到dbQueryResult对象中
            DBQueryByMabtisResult dbQueryByMybatisResult = new DBQueryByMabtisResult();
            dbQueryByMybatisResult.setTableName(tableName);
            dbQueryByMybatisResult.setColumnLabelAndValues(columnLabelAndValues);
            System.out.println("dbQueryByMybatisResult"+dbQueryByMybatisResult);
           // 将对象添加到list集合中
            dbCheckByMybatisResults.add(dbQueryByMybatisResult);

        }
        //将集合转换成字符串
        return JSONObject.toJSONString(dbCheckByMybatisResults);
    }

    public static void main(String[] args) {
        String sqllist="[{\"tableName\":\"emp\",\"sql\":\"select e.empid,e.ename,e.esex,e.deptid from emp e where e.empid='2' \"},{\"tableName\":\"dept\",\"sql\":\"select d.deptid,d.dname,d.dloc from dept d where d.deptid='2' \"}]";

        List<DBCheckerByMabatis> dbCheckers = JSONObject.parseArray(sqllist, DBCheckerByMabatis.class);
        for (DBCheckerByMabatis dbChecker : dbCheckers) {
            System.out.println(dbChecker);
        }
    }
}
