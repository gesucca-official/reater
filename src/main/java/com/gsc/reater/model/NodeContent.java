package com.gsc.reater.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class NodeContent implements Serializable {
    private String token;
    private String pos;

    @Override
    public String toString() {
        return token + "<" + pos + ">";
    }
}
