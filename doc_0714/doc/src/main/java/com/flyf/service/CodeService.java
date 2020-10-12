package com.flyf.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flyf.entity.Code;
import com.flyf.mapper.CodeMapper;
import org.springframework.stereotype.Service;

@Service
public class CodeService extends ServiceImpl<CodeMapper, Code> {
}
