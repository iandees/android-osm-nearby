package com.yellowbkpk.osmnearby.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Primitive {

    private Map<String, String> tags = new HashMap<String, String>();
    private long id;

    public Primitive(long id) {
        this.id = id;
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
