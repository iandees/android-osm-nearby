package com.yellowbkpk.osmnearby.model;

import java.util.LinkedList;

public class Way extends Primitive {

    private LinkedList<Node> nodes = new LinkedList<Node>();

    public Way(long id) {
        super(id);
    }
    
    public void addNode(Node n) {
        this.nodes.add(n);
    }

    public Node getFirstNode() {
        return this.nodes.getFirst();
    }
}
