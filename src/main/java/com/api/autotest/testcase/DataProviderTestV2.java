package com.api.autotest.testcase;

import com.api.autotest.util.CaseUtil;
import org.testng.annotations.DataProvider;

/**
 * 存在问题：
 * 1、大量存在行号和列号
 * 2、修改用例后，代码需要修改，维护成本高
 * 3、重复读取
 */
public class DataProviderTestV2 extends BaseProcessorV9 {

    /**
     * 获取接口2的所有测试数据
     *
     * @return
     */
    @DataProvider
    public Object[][] datas1() {

        String[] cellNames = {"CaseId", "ApiId", "Params", "ExpectedResponseData", "PreVerifyDataSql", "AfterVerifyDataSql"};
        Object[][] datas = CaseUtil.getCaseDatasByApiId("1", cellNames);
        return datas;
    }

}
