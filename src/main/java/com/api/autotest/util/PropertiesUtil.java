package com.api.autotest.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
    /**
     * 读取jdbc.properties文件
     */
    private static Properties properties;

    public static Properties getJdbcProp() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("Properties/jdbc.properties"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static Properties getExcelProp() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream("Properties/excelconfig.properties"),"UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String getExcelPathFromProp() {
        String excelpath=null;
        try {
            properties= PropertiesUtil.getExcelProp();
            excelpath = properties.getProperty("excelpath");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelpath;
    }

    public static String getInterfaceInfoNameFromProp() {
        String interfaceInfoName=null;
        try {
            properties= PropertiesUtil.getExcelProp();
            interfaceInfoName = properties.getProperty("interfaceinfoSheetName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return interfaceInfoName;
    }

    public static String getCaseSheetNameProp() {
        String caseSheetName=null;
        try {
            properties= PropertiesUtil.getExcelProp();
            caseSheetName = properties.getProperty("caseSheetName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseSheetName;
    }

    public static String getVariableSheetNameProp() {
        String variableSheetName=null;
        try {
            properties= PropertiesUtil.getExcelProp();
            variableSheetName = properties.getProperty("variableSheetName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return variableSheetName;
    }

    public static void main(String[] args) {
        Properties excelProp = PropertiesUtil.getExcelProp();
        String excelpath = excelProp.getProperty("excelpath");
        System.out.println(excelpath);
        String interfaceinfoSheet = excelProp.getProperty("interfaceinfoSheet");
        System.out.println(interfaceinfoSheet);
        String caseSheet = excelProp.getProperty("caseSheet");
        System.out.println(caseSheet);
        String variableSheet = excelProp.getProperty("variableSheet");
        System.out.println(variableSheet);


    }
}
