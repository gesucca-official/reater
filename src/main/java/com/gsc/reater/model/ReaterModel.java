package com.gsc.reater.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
public class ReaterModel implements Serializable {
    private final Network tokensNetwork = new Network();
    private final Network posNetwork = new Network();

    private final Set<Integer> lengths = new HashSet<>();

    public double getAvgLength() {
        return lengths.stream()
                .mapToDouble(value -> (double) value)
                .reduce(Double::sum)
                .orElse(0) / lengths.size();
    }

    public double getLengthsStdDev() {
        final double avg = getAvgLength();
        return Math.sqrt(lengths.stream()
                .map(l -> Math.pow(l - avg, 2))
                .reduce(Double::sum)
                .orElse(0.0) / lengths.size());
    }

    @Override
    public String toString() {
        return "*** Tokens Network ***\n" +
                tokensNetwork +
                "*** POS Network ***\n" +
                posNetwork +
                "*** Average Sentence Length: " + getAvgLength() + "\n" +
                "*** Lengths Std Dev: " + getLengthsStdDev();
    }
}
