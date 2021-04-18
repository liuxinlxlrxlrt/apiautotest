package com.api.autotest.util;

import com.api.autotest.pojo.Variable;
import com.api.autotest.pojo.WriteBackData;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 参数化工具类
 */
public class VariableConvertUtil {
    private static Logger logger = Logger.getLogger(VariableConvertUtil.class);
    //list保存所有的变量数据
    public static List<Variable> variables = new ArrayList<>();

    //匹配变量name和value
    public static Map<String, String> variableNameMappingValue = new HashMap<>();

    static {
        //第一步加载表单中的数据一次将每行封装成对象，然后统一添加到集合中
        List<Variable> variableList = ExcelUtils.load(ExcelUtils.casePath, "变量", Variable.class);
        variables.addAll(variableList);

        /**
         * 变量表：
         * 1、获取rowIdentifier以及对应的行索引添加到rowIdentifierMappingRowNum（map中）
         * 2、获取cellName以及它对应的列索引添加到cellNameMappingCellNum（map中）
         */
        ExcelUtils.loadRownumAndCellnumMapping(ExcelUtils.casePath, "变量");

        //1、将变量名以及对应变量的值加载到map集合、
        //2、将动态变量通过反射方法获取出来，添加到回写数据中
        loadVarablesToMap();

    }

    /**
     * 1、遍历变量集合，将所有的变量名和对应的变量值保存到map集合
     * 2、将动态变量通过反射方法获取出来，添加到回写数据中
     */
    public static void loadVarablesToMap() {
        for (Variable variable : variables) {
            //获取变量名
            String variableName = variable.getName();
            //获取到变量名的对应value
            String variableValue = variable.getValue();
            //如果value为空
            if (variableValue == null || variableValue.trim().length() == 0) {
                //获取变量值为空的"类的全名"
                String reflectClass = variable.getReflectClass();
                String reflectMethod = variable.getReflectMethod();
                try {
                    //通过反射获取类的字节码
                    Class clazz = Class.forName(reflectClass);
                    //通过类的class对象的newInstance方法创建对象(该类必须要有无参构造方法)
                    Object obj = clazz.newInstance();
                    //获取要调用的方法对象
                    Method method = clazz.getMethod(reflectMethod);
                    //调用invoke()方法执行MobilePhoneGenerator类中的方法获取返回值
                    variableValue = (String) method.invoke(obj);

                    //保存要回写的数据到集合
                    ExcelUtils.writeBackDatas.add(new WriteBackData("变量", variableName, "ReflectValue", variableValue));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("错误日志");
                }
            }
            logger.info("变量名称：" + variableName + "，变量值：" + variableValue);
            variableNameMappingValue.put(variableName, variableValue);

        }
    }

    /**
     * 替换参数的变量
     *
     * @param paramter
     * @return
     */
    public static String replaceVariable(String paramter) {
        /**
         * 1、在 for 循环中使用 entries 实现 Map 的遍历（最常见和最常用的）
         *
         * 2、使用 for-each 循环遍历 key 或者 values，性能上比 entrySet 较好
         * 一般适用于只需要 Map 中的 key 或者 value 时使用
         *
         */
        //使用 for-each 循环遍历 key 或者 values,使用map.keySet()
        Set<String> variableNames = variableNameMappingValue.keySet();
        for (String variableName : variableNames) {
            //如果测试数据汇总出现了变量名
            if (paramter.contains(variableName)) {
                //String的replace() 替换方法
                paramter = paramter.replace(variableName, variableNameMappingValue.get(variableName));
            }
        }
        return paramter;
    }

//    public static void main(String[] args) {
//        for (Variable variable : variables) {
//            System.out.println(variable);
//        }
//    }

}
