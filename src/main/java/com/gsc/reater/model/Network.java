package com.gsc.reater.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
public class Network implements Serializable {

    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, Integer> entryPoints = new HashMap<>();

    public Node deepSearch(NodeContent content) {
        return nodes.get(content.hashCode());
    }

    public Node deepSearch(int contentHash) {
        return nodes.get(contentHash);
    }

    public void addEntryPoint(Node node) {
        if (entryPoints.containsKey(node.getContent().hashCode()))
            entryPoints.put(node.getContent().hashCode(), entryPoints.get(node.getContent().hashCode()) + 1);
        else this.entryPoints.put(node.getContent().hashCode(), 1);
        addNode(node);
    }

    public void addNode(Node node) {
        if (nodes.containsKey(node.getContent().hashCode()))
            nodes.get(node.getContent().hashCode()).mergeNode(node);
        else nodes.put(node.getContent().hashCode(), node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("||| NODES |||\n");
        for (Node n : nodes.values())
            sb.append(n).append("\n");
        return sb.toString();
    }
}
