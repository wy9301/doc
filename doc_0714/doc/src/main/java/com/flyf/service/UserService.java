package com.flyf.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyf.entity.User;
import com.flyf.mapper.UserMapper;
import org.springframework.stereotype.Service;

//实现类继承ServiceImpl<操作实体的Mapper接口，具体实体类>，
// 最后添加注解@Service将该类作为Spring容器下的Bean。
//ServiceImpl定义了Mapper及Entity的泛型，编写具体的服务层实现类时，
//传入对应的mapper和entity，即可实现mapper的传递及初始化
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
