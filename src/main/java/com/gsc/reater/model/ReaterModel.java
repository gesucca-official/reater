package com.gsc.reater.model;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ReaterModel implements Serializable {
    private final Network tokensNetwork = new Network();
    private final Network posNetwork = new Network();

    @Override
    public String toString() {
        return "*** Tokens Network ***\n" +
                tokensNetwork +
                "*** POS Network ***\n" +
                posNetwork;
    }
}
