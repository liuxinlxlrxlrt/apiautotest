package com.api.autotest.mapper;

import org.apache.ibatis.annotations.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Annotated注解
 */
@Mapper
public interface SqlMapperAnnotation {
    @Insert("${insertSql}")
    int insert(@Param(value ="insertSql") String insertSql);
    @Update("${updateSql}")
    int update(@Param(value ="updateSql") String updateSql);
    @Delete("${deleteSql}")
    int delete(@Param(value ="deleteSql") String deleteSql);
    @Select("${selectSql}")
    List<LinkedHashMap<String,Object>> query(@Param(value ="selectSql") String selectSql);
}
