package com.gsc.reater.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class ReaterModel implements Serializable {

    private final Network network = new Network();

    @Setter
    double avgLength, lengthsStdDev;

    @Override
    public String toString() {
        return "*** Reater Model ***\n" +
                network +
                "** Average Sentence Length: " + getAvgLength() + "\n" +
                "** Lengths Std Dev: " + getLengthsStdDev();
    }
}
