package com.flyf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

/*实体类*/
@Data//省略get set方法
@TableName("Share_code")
public class Code {
    @TableId(type = IdType.AUTO)
    private long id;
    private int docid;
    private String code;
    private Date time;
    private int days;

}
