package com.api.autotest.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回写数据对象
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteBackData {
    /**
     * sheet名称
     */
    private String sheetName;
    /**
     * 行标识
     */
    private String rowIdentifier;
    /**
     * 列名
     */
    private String cellName;
    /**
     * 要写入的数据
     */
    private String result;

    @Override
    public String toString(){
        return "sheetName="+sheetName+",rowIdentifier="+rowIdentifier+",cellName="+cellName+",result="+result;
    }
}
