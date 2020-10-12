package com.flyf.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyf.entity.MyFile;
import com.flyf.mapper.MyFileMapper;
import org.springframework.stereotype.Service;

@Service
public class MyFileService extends ServiceImpl<MyFileMapper, MyFile> {
}
