package com.api.autotest.util;

import org.testng.Assert;

public class AssertUtil {

    public static String assertEquals(String actualResponseData,String expectedResponseData){
        String result = "Pass";

        try {
            Assert.assertEquals(actualResponseData,expectedResponseData);
        } catch (Throwable e) {
            result = actualResponseData;
        }
        return result;
    }
}
