package com.gsc.reater.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class Node implements Serializable {

    private final NodeContent content;
    private int weight = 1;

    private final Map<Integer, Integer> links = new HashMap<>();
    private final Set<List<String>> previousPosChains = new HashSet<>();

    public Node(String token, String pos) {
        this.content = new NodeContent(token, pos);
    }

    public void addLinkTo(Node destination) {
        if (links.containsKey(destination.getContent().hashCode()))
            links.put(destination.getContent().hashCode(), links.get(destination.getContent().hashCode()) + 1);
        else links.put(destination.getContent().hashCode(), 1);
    }

    public void mergeNode(Node other) {
        if (!other.content.equals(this.content))
            throw new IllegalArgumentException("Trying to merge nodes with two different contents!");
        this.weight += other.weight;
        this.previousPosChains.addAll(other.previousPosChains);
        other.links.forEach((key, value) -> this.links.merge(key, value, Integer::sum));
    }

    @Override
    public String toString() {
        return "(" + content + ")-" + previousPosChains;
    }
}
