package com.yellowbkpk.osmnearby.model;

import java.util.Collections;
import java.util.Map;

public class OsmData {

    private Map<Long, Node> nodes;
    private Map<Long, Way> ways;

    public OsmData(Map<Long, Node> nodes, Map<Long, Way> ways) {
        this.nodes = nodes;
        this.ways = ways;
    }

    public Map<Long, Node> getNodes() {
        return Collections.unmodifiableMap(this.nodes);
    }

    public Map<Long, Way> getWays() {
        return Collections.unmodifiableMap(this.ways);
    }
}
