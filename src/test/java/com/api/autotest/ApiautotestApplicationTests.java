package com.api.autotest;


import com.api.autotest.util.MybatisUtil;
import org.testng.annotations.Test;

import java.util.Map;

public class ApiautotestApplicationTests {

    @Test
    public void contextLoads() {

        Map<String, String> query = MybatisUtil.query("select * from member");
        System.out.println(query);


    }

}
