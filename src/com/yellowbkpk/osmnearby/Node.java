package com.yellowbkpk.osmnearby;

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
}
