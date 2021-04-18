package com.api.autotest.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    /**
     * 读取jdbc.properties文件
     */
    private static Properties properties;

    public static Properties getJdbcProp(){
        try {
            if(properties == null){
                properties =new Properties();
                properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("Properties/jdbc.properties"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
