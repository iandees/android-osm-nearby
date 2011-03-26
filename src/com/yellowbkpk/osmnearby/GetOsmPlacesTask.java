package com.yellowbkpk.osmnearby;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.yellowbkpk.osmnearby.model.Node;
import com.yellowbkpk.osmnearby.model.OsmData;
import com.yellowbkpk.osmnearby.model.Way;

public class GetOsmPlacesTask extends AsyncTask<Location, Void, List<OsmPlace>> {

    private static final double BUFFER = 0.007;
    private static final String TAG = GetOsmPlacesTask.class.getName();
    private static final DistanceComparator distanceComparator = new DistanceComparator();
    private static final NumberFormat FLOAT_FORMAT = NumberFormat.getInstance();
    static {
        FLOAT_FORMAT.setGroupingUsed(false);
        FLOAT_FORMAT.setMaximumFractionDigits(6);
    }

    @Override
    protected List<OsmPlace> doInBackground(Location... params) {
        OsmData data = null;
        try {
            URL u = new URL("http://jxapi.openstreetmap.org/xapi/api/0.6/*[amenity|leisure|tourism=*][bbox="
                    + buildBbox(params[0]) + "]");
            URLConnection connection = u.openConnection();
            InputStream inputStream = connection.getInputStream();

            OsmParser parser = new OsmParser();
            data = parser.parse(inputStream);

            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Found " + data.getNodes().size() + " nodes and " + data.getWays().size() + " ways.");

        List<OsmPlace> places = new ArrayList<OsmPlace>(data.getWays().size());
        Collection<Way> waysColl = data.getWays().values();
        for (Way way : waysColl) {
            Node firstNode = way.getFirstNode();
            String name = way.getTag("name");
            if (name != null) {
                OsmPlace place = new OsmPlace(name, firstNode.getLocation(), way);
                places.add(place);
            }
        }

        Collection<Node> nodesColl = data.getNodes().values();
        for (Node node : nodesColl) {
            String name = node.getTag("name");
            if (name != null) {
                OsmPlace place = new OsmPlace(name, node.getLocation(), node);
                places.add(place);
            }
        }

        distanceComparator.currentLocation = params[0];
        Collections.sort(places, distanceComparator);

        for (OsmPlace osmPlace : places) {
            if (osmPlace.getName() != null) {
                Log.i(TAG, "Place Name: " + osmPlace.getName());
            }
        }

        return places;
    }

    private String buildBbox(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        double left = lon - BUFFER;
        double right = lon + BUFFER;
        double top = lat + BUFFER;
        double bottom = lat - BUFFER;

        return new StringBuilder(32)
                .append(FLOAT_FORMAT.format(left))
                .append(",")
                .append(FLOAT_FORMAT.format(bottom))
                .append(",")
                .append(FLOAT_FORMAT.format(right))
                .append(",")
                .append(FLOAT_FORMAT.format(top)).toString();
    }

}
