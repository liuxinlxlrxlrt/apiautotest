package com.api.autotest.variable;


import com.api.autotest.util.JDBCUtil;

import java.util.Map;

public class MobilePhoneGenerator {

    /**
     * 生成手机注册手机号
     */
    public String generateToRegisteredMobilePhone(){
        //CONCAT(str1,str2,…) 返回结果为连接参数产生的字符串。如有任何一个参数为NULL ，则返回值为 NULL
        String sql = "select concat(MAX(mobilephone)+1,'') as toRegisteredMobilePhone from member";
        Map<String, Object> columnLableAndValues = JDBCUtil.query(sql);
        return columnLableAndValues.get("toRegisteredMobilePhone").toString();

    }

    /**
     * 生成系统中还未注册的手机号
     */
    public String  generateSystemNotExistedMobilePhone(){
        //sql中的concat函数，拼接
        String sql = "select concat(MAX(mobilephone)+2,'') as systemNotExistedMobilePhone from member";
        Map<String, Object> columnLableAndValues = JDBCUtil.query(sql);
        return columnLableAndValues.get("systemNotExistedMobilePhone").toString();
    }

//    public static void main(String[] args) {
//        MobilePhoneGenerator phoneGenerator = new MobilePhoneGenerator();
//        String s1 = phoneGenerator.generateSystemNotExistedMobilePhone();
//        //columnValue：1.8813998451E10,因为将它当成一个很大的整数
//        System.out.println("s1:"+s1);
//        String s2 = phoneGenerator.generateToRegisteredMobilePhone();
//        System.out.println("s2:"+s2);
//    }
}
