package com.api.autotest.testcase;

import com.alibaba.fastjson.JSONObject;
import com.api.autotest.pojo.WriteBackData;
import com.api.autotest.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口测试统一处理类
 */
public class BaseProcessorV8 {

    private static Logger logger = Logger.getLogger(BaseProcessorV8.class);

    String[] cellNames = {"CaseId", "ApiId", "Params", "ExpectedResponseData", "PreVerifyDataSql", "AfterVerifyDataSql"};

    @Test(dataProvider = "datas")
    public void test(String caseId, String apiId, String params, String expectedResponseData,
                       String preVerifyDataSql, String afterVerifyDataSql) throws UnsupportedEncodingException {
        logger.info("调用接口前的数据验证");
        //执行“接口调用前”数据验证
        if (preVerifyDataSql != null && preVerifyDataSql.trim().length() > 0) {

            //查询前sql中替换掉变量
            preVerifyDataSql = VariableConvertUtil.replaceVariable(preVerifyDataSql);

            //在接口调用前查询我们想要验证的字段
            String preVerifyDataResult = DBCheckUtil.doQuery(preVerifyDataSql);

            //保存回写结果
            ExcelUtils.writeBackDatas.add(new WriteBackData("用例", caseId, "PreVerifyDataResult", preVerifyDataResult));

        }
        logger.info("根据接口编号【"+apiId+"】获取接口请求地址");
        //根据ApiId(接口编号)获取接口地址
        String url = RestUtil.getUrlByApiId(apiId);
        logger.info("根据接口编号【"+apiId+"】获取接口提交类型");
        //根据ApiId(接口编号)获取接口提交类型
        String type = RestUtil.getTypeByApiId(apiId);
        logger.info("替换变量");
        //替换测试数据中的所有变量
        params = VariableConvertUtil.replaceVariable(params);

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        //将所有参数解析到map
        Map<String, String> parameters = (Map<String, String>) JSONObject.parse(params);
        logger.info("开始调用接口");
        //调用doService方法完成接口调用，拿到接口响应报文
        String actualResponseData = HttpClientUtil.doService(url, type, parameters, header);

        //断言比较实际结果、期望值
        actualResponseData = AssertUtil.assertEquals(actualResponseData, expectedResponseData);


        //保存回写对象

        /**
         * 文件重复读写，频繁操作IO流，影响性能开销，执行代码比慢
         * 解决办法：先把每条数据保存起来，把result疯封装成对象，最后只写一次
         * result+sheetName+caseId+cellName
         */
        //保存回写结果
        ExcelUtils.writeBackDatas.add(new WriteBackData("用例", caseId, "ActualResponseData", actualResponseData));
        logger.info("接口调用后的数据验证");
        //执行接口调用后数据验证
        if (afterVerifyDataSql != null && afterVerifyDataSql.trim().length() > 0) {

            //查询前sql中替换掉变量
            afterVerifyDataSql = VariableConvertUtil.replaceVariable(afterVerifyDataSql);

            //在接口调用后查询我们想要验证的字段
            String afterVerifyDataResult = DBCheckUtil.doQuery(afterVerifyDataSql);

            //保存回写结果
            ExcelUtils.writeBackDatas.add(new WriteBackData("用例", caseId, "AfterVerifyDataResult", afterVerifyDataResult));
        }
    }


    /**
     * 批量回写数据
     */
    @AfterSuite
    public void batchWriteBackDate() {
        ExcelUtils.batchWriteBackDate(ExcelUtils.casePath);
    }


//    @Test(dataProvider = "test1")
//    public void test01(String caseId, String apiId, String params, String expectedResponseData,
//                       String preVerifyDataSql, String afterVerifyDataSql) throws UnsupportedEncodingException {
//
//        if (preVerifyDataSql != null && preVerifyDataSql.trim().length() > 0) {
//
//            //在接口调用前查询我们想要验证的字段
//            String preVerifyDataResult = DBCheckUtil.doQuery(preVerifyDataSql);
//            System.out.println("preVerifyDataResult" + preVerifyDataResult);
//            ExcelUtils.writeBackDatas.add(new WriteBackData(sheetName2, caseId, "PreVerifyDataResult", preVerifyDataResult));
//
//        }
//
//        //根据接口编号拿接口地址
//        String url = RestUtil.getUrlByApiId(apiId);
//        String type = RestUtil.getTypeByApiId(apiId);
//
//        //添加headers
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", "application/x-www-form-urlencoded");
//        Map<String, String> parameters = (Map<String, String>) JSONObject.parse(params);
//
//        //执行请求
//        String actualResponseData = HttpClientUtil.doService(url, type, parameters, header);
//        System.out.println("result=" + actualResponseData);
//
//        //对比实际结果、期望结果
//        actualResponseData = AssertUtil.assertEquals(actualResponseData, expectedResponseData);
//        //保存回写对象
//        ExcelUtils.writeBackDatas.add(new WriteBackData(sheetName2, caseId, actualResponseDataName, actualResponseData));
//
//
//        if (afterVerifyDataSql != null && afterVerifyDataSql.trim().length() > 0) {
//            //在接口调用后查询我们想要验证的字段
//            String afterVerifyDataResult = DBCheckUtil.doQuery(afterVerifyDataSql);
//            System.out.println("afterVerifyDataResult" + afterVerifyDataResult);
//            ExcelUtils.writeBackDatas.add(new WriteBackData(sheetName2, caseId, "AfterVerifyDataResult", afterVerifyDataResult));
//        }
//    }
}

