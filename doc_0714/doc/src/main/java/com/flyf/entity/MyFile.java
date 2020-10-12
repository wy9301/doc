package com.flyf.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@Data//省略get set方法
@TableName("DOC_FILE")
public class MyFile {
    @TableId(type = IdType.AUTO)
    private long id;
    private String name;
    private String size;
    private String master;
    private String visible;
    private String category;
    private String address;
    private Date updatetime;

    private int recycle;
    private Date recycletime;
    private long days;

}
