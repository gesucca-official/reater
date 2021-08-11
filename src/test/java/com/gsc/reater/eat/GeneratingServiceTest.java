package com.gsc.reater.eat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GeneratingServiceTest {

    @Autowired
    GeneratingService service;

    @Test
    void generate() {
        System.out.println(
                service.generateSentences("input/model.eat", 2)
        );
    }

}