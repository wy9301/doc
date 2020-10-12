package com.flyf.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data//省略get set方法
@TableName("DOC_RELATION")
public class Relation {
    Integer id1;
    Integer id2;
}
