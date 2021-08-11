package com.gsc.reater.eat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EatingServiceTest {

    @Autowired
    EatingService service;

    @Test
    void eat() {
        service.eat("input/il_senno_di_poi_min.txt", "input/model.eat");
    }
}
