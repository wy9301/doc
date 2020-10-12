package com.flyf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

/*实体类*/
@Data//省略get set方法
@TableName("statistics")
public class Statistics {

    private Date theday;
    private Integer upload;
    private Integer download;
}
