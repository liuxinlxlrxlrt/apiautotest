package com.api.autotest.pojo;

import lombok.Data;

@Data
public class Case {
    private String caseId;
    private String apiId;
    private String desc;
    private String params;
    private String expectedResponseData;
    private String actualResponseData;
    private String preVerifyDataSql;
    private String preVerifyDataResult;
    private String afterVerifyDataSql;
    private String afterVerifyDataResult;

    @Override
    public String toString() {
        return "caseId=" + caseId + ",apiId=" + apiId + ",desc=" + desc + ",params=" + params + "" + ",expectedResponseData" + expectedResponseData + ",actualResponseData" + actualResponseData;
    }
}
