package com.gsc.reater.service.gen;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class GeneralMathServiceImpl implements GeneralMathService {

    @Override
    public int randomToMax(int max) {
        return ThreadLocalRandom.current().nextInt(0, max + 1);
    }
}
