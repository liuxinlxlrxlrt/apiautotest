package com.api.autotest.util;

import com.alibaba.fastjson.JSONObject;
import com.api.autotest.pojo.DBChecker;
import com.api.autotest.pojo.DBQueryResult;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBCheckUtil {
    /**
     * 根据脚本执行查询并返回查询结果
     * @param verifySql
     * @return
     */
    public static String doQuery(String verifySql){
        List<DBQueryResult> dbQueryResults =new ArrayList();
        //将脚本字符串封装成了list对象
        List<DBChecker> dbCheckers = JSONObject.parseArray(verifySql, DBChecker.class);
        //循环遍历，取出sql执行
        for (DBChecker dbChecker : dbCheckers) {
            String no = dbChecker.getNo();
            System.out.println("no："+no);
            String sql = dbChecker.getSql();
            System.out.println("sql："+sql);
            //执行查询，获取结果
            Map<String, Object> columnLabelAndValues = JDBCUtil.query(sql);
//            Set<Map.Entry<String, Object>> entries = columnLabelAndValues.entrySet();
//            for (Map.Entry<String, Object> entry : entries) {
//                System.out.print("key:"+entry.getKey()+",value:"+entry.getValue());
//            }
            //将结果放到dbQueryResult对象中
            DBQueryResult dbQueryResult = new DBQueryResult();
            dbQueryResult.setNo(no);
            dbQueryResult.setColumnLabelAndValues(columnLabelAndValues);
//            System.out.println("dbQueryResult"+dbQueryResult);
            //将对象添加到list集合中
            dbQueryResults.add(dbQueryResult);

        }
        //将集合转换成字符串
        return JSONObject.toJSONString(dbQueryResults);
    }

//    public static void main(String[] args) {
//        String sqllist="[{\"no\":\"1\",\"sql\":\"select leaveamount from member where mobilephone='18813998449' \"},{\"no\":\"2\",\"sql\":\"select count(*) as totalNum from financelog where IncomeMenberId=(select id from member where mobilephone='18813998449') \"}]";
//
//        List<DBChecker> dbCheckers = JSONObject.parseArray(sqllist, DBChecker.class);
//        for (DBChecker dbChecker : dbCheckers) {
//            System.out.println(dbChecker);
//        }
//    }
}
