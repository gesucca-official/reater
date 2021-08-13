package com.gsc.reater.service.gen;

import java.util.Set;

public interface GeneralMathService {
    int randomToMax(int max);

    double avgLength(Set<Integer> lengths);

    double lengthsStdDev(Set<Integer> lengths);

}
