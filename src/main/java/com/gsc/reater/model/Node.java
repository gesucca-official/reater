package com.gsc.reater.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class Node implements Serializable {

    private final String content;
    private int weight = 1;

    private final Map<String, Integer> links = new HashMap<>();
    private final Map<String, String> metaData = new HashMap<>();

    public Node(String content) {
        this.content = content;
    }

    public boolean isTerminal() {
        return links.size() == 0;
    }

    public void addLinkTo(Node destination) {
        if (links.containsKey(destination.getContent()))
            links.put(destination.getContent(), links.get(destination.getContent()) + 1);
        else links.put(destination.getContent(), 1);
    }

    public void mergeNode(Node other) {
        if (!other.content.equals(this.content))
            throw new IllegalArgumentException("Trying to merge nodes with two different contents!");
        this.weight += other.weight;
        this.metaData.putAll(other.getMetaData());
        other.links.forEach((key, value) -> this.links.merge(key, value, Integer::sum));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(content, node.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "(" + content + ") - " + metaData + " -> " + links;
    }
}
