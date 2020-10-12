package com.flyf.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.flyf.entity.Statistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface StatisticsMapper extends BaseMapper<Statistics> {
    /**
     * 和Mybatis使用方法一致
     * @param getType
     * @return
     */
    @Select("select sum(#{getType}) from statistics")
    Integer getsta(@Param("getType") String getType);

}
