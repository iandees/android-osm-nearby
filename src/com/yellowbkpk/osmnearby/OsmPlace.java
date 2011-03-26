package com.yellowbkpk.osmnearby;

import java.util.Collections;
import java.util.Map;

import com.yellowbkpk.osmnearby.model.Primitive;

import android.location.Location;

public class OsmPlace {

    private String name;
    private Location loc;
    private Primitive osm;

    public OsmPlace(String name, Location location, Primitive osm) {
        this.name = name;
        this.loc = location;
        this.osm = osm;
    }

    public String toString() {
        return this.name;
    }

    public Location getLoc() {
        return this.loc;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getTags() {
        return this.osm.getTags();
    }

    public Primitive getOsmPrimitive() {
        return osm;
    }
}
