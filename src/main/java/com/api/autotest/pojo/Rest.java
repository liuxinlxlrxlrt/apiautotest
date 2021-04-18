package com.api.autotest.pojo;

import lombok.Data;

@Data
public class Rest {
    private String apiId;
    private String apiName;
    private String type;
    private String url;

    @Override
    public String toString(){
        return "apiId="+apiId+",apiName="+apiName+",type="+type+",url="+url;
    }
}
