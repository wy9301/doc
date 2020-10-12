package com.flyf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyf.entity.Code;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CodeMapper extends BaseMapper<Code> {

    //此处由BaseMapper提供了很多增删改查方法,无需在自己定义
}