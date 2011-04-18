package com.yellowbkpk.osmnearby.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Primitive {

    private final Map<String, String> tags;
    private final long id;

    public Primitive(long id, Map<String, String> tags) {
        this.id = id;
        this.tags = tags;
    }
    
    public Primitive(long id) {
        this(id, new HashMap<String, String>());
    }

    public void setTag(String key, String val) {
        tags.put(key, val);
    }

    public long getId() {
        return id;
    }

    public String getTag(String key) {
        return this.tags.get(key);
    }

    public Map<String, String> getTags() {
        return Collections.unmodifiableMap(this.tags);
    }

    public abstract String getKind();
}
