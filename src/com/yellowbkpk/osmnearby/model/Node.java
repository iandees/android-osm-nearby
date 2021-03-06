package com.yellowbkpk.osmnearby.model;

import android.location.Location;


public class Node extends Primitive {

    private Location loc;

    public Node(long id, Location ll) {
        super(id);
        this.loc = ll;
    }

    public Location getLocation() {
        return this.loc;
    }

    @Override
    public String getKind() {
        return "node";
    }
}
