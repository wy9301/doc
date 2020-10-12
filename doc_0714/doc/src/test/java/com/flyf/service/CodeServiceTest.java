package com.flyf.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CodeServiceTest {
    @Autowired
    CodeService codeService;

    @Test
    public void list(){
        int id=18;
        String str=",2,18,";
        str=str.replace(",18,",",");
        System.out.println(str);
//        codeService.list().forEach(System.out::println);
    }
}