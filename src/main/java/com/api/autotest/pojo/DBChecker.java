package com.api.autotest.pojo;

import lombok.Data;

@Data
public class DBChecker {
    private String no;
    private String sql;

    @Override
    public String toString(){
        return "no="+no +",sql="+sql;
    }
}
