package com.yellowbkpk.osmnearby;

import java.util.Comparator;

import android.location.Location;

public class DistanceComparator implements Comparator<OsmPlace> {

    public Location currentLocation;

    @Override
    public int compare(OsmPlace object1, OsmPlace object2) {
        if (currentLocation != null) {
            Location a = object1.getLoc();
            Location b = object2.getLoc();
            float d1 = currentLocation.distanceTo(a);
            float d2 = currentLocation.distanceTo(b);
            return (d2 > d1) ? -1 : 1;
        } else {
            return 0;
        }
    }

}
