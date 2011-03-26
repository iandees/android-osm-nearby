package com.yellowbkpk.osmnearby;

import java.util.Collections;
import java.util.Map;

import android.location.Location;

public class OsmPlace {

    private String name;
    private Location loc;
    private Map<String, String> tags;

    public OsmPlace(String name, Location location, Map<String, String> tags) {
        this.name = name;
        this.loc = location;
        this.tags = tags;
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
        return Collections.unmodifiableMap(this.tags);
    }
}
