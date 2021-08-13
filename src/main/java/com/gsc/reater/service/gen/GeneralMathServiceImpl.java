package com.gsc.reater.service.gen;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GeneralMathServiceImpl implements GeneralMathService {

    @Override
    public int randomToMax(int max) {
        return ThreadLocalRandom.current().nextInt(0, max + 1);
    }

    @Override
    public double avgLength(Set<Integer> lengths) {
        return lengths.stream()
                .mapToDouble(value -> (double) value)
                .reduce(Double::sum)
                .orElse(0) / lengths.size();
    }

    @Override
    public double lengthsStdDev(Set<Integer> lengths) {
        final double avg = avgLength(lengths);
        return Math.sqrt(lengths.stream()
                .map(l -> Math.pow(l - avg, 2))
                .reduce(Double::sum)
                .orElse(0.0) / lengths.size());
    }

}
