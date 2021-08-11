package com.gsc.reater.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
public class Network implements Serializable {

    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, Integer> entryPoints = new HashMap<>();

    public Node entryPointsSearch(String content) {
        if (entryPoints.containsKey(content))
            return nodes.get(content);
        else return null;
    }

    public Node deepSearch(String content) {
        return nodes.get(content);
    }

    public void addEntryPoint(Node node) {
        if (entryPoints.containsKey(node.getContent()))
            entryPoints.put(node.getContent(), entryPoints.get(node.getContent()) + 1);
        else this.entryPoints.put(node.getContent(), 1);
        addNode(node);
    }

    public void addNode(Node node) {
        if (nodes.containsKey(node.getContent()))
            nodes.get(node.getContent()).mergeNode(node);
        else nodes.put(node.getContent(), node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entry Points: ").append(entryPoints);
        sb.append("\n||| NODES |||\n");
        for (Node n : nodes.values())
            sb.append(n).append("\n");
        return sb.toString();
    }
}
