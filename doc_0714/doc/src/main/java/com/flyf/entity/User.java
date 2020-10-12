package com.flyf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

/*实体类*/
@Data//省略get set方法
@TableName("DOC_USER")
public class User {
    @TableId(type = IdType.AUTO)
    private long id;
    private String username;
    private String password;
    private  String tel;
    private Date intime;
    private Integer right1;
    private String head;

}
