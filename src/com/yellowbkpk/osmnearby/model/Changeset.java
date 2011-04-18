package com.yellowbkpk.osmnearby.model;

import java.util.Map;

public class Changeset extends Primitive {

    public Changeset(long id, Map<String, String> tags) {
        super(id, tags);
    }

    @Override
    public String getKind() {
        return "changeset";
    }

}
