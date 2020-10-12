package com.flyf.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RelationServiceTest {
    @Autowired
    RelationService relationService;

    @Test
    public void list(){

        relationService.list().forEach(System.out::println);
    }
}