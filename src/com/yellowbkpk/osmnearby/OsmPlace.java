package com.yellowbkpk.osmnearby;

import android.location.Location;

public class OsmPlace {

    private String name;
    private Location loc;

    public OsmPlace(String name, Location location) {
        this.name = name;
        this.loc = location;
    }

    public String toString() {
        return this.name;
    }

    public Location getLoc() {
        return loc;
    }

    public String getName() {
        return name;
    }
}
