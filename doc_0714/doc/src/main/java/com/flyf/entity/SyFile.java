package com.flyf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@Data//省略get set方法
@TableName("DOC_SYFILE")
public class SyFile {
    @TableId(type = IdType.AUTO)
    private long id;
    private String name;
    private String size;
    private String master;
    private String category;
    private String address;
    private Date updatetime;
}
