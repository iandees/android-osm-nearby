package com.yellowbkpk.osmnearby;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.yellowbkpk.osmnearby.model.Node;
import com.yellowbkpk.osmnearby.model.Primitive;
import com.yellowbkpk.osmnearby.model.Way;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class GetOsmPlacesTask extends AsyncTask<Location, Void, List<OsmPlace>> {

    private static final double BUFFER = 0.007;
    private static final String TAG = GetOsmPlacesTask.class.getName();
    private static final DistanceComparator distanceComparator = new DistanceComparator();
    private Map<Long, Node> nodes = new HashMap<Long, Node>();
    private Map<Long, Way> ways = new HashMap<Long, Way>();
    private Primitive currentPrim;

    @Override
    protected List<OsmPlace> doInBackground(Location... params) {
        try {
            URL u = new URL("http://jxapi.openstreetmap.org/xapi/api/0.6/*[amenity|leisure|tourism=*][bbox="
                    + buildBbox(params[0]) + "]");
            URLConnection connection = u.openConnection();
            InputStream inputStream = connection.getInputStream();

            XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
            pullParser.setInput(inputStream, "UTF-8");
            int eventType = pullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = pullParser.getName();
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    if ("tag".equals(name)) {
                        String key = pullParser.getAttributeValue(null, "k");
                        String val = pullParser.getAttributeValue(null, "v");
                        currentPrim.setTag(key, val);
                    } else if ("node".equals(name)) {
                        long id = Long.parseLong(pullParser.getAttributeValue(null, "id"));
                        Location latlon = new Location("myself");
                        double lat = Double.parseDouble(pullParser.getAttributeValue(null, "lat"));
                        latlon.setLatitude(lat);
                        double lon = Double.parseDouble(pullParser.getAttributeValue(null, "lon"));
                        latlon.setLongitude(lon);
                        currentPrim = new Node(id, latlon);
                    } else if ("nd".equals(name)) {
                        String ndIdStr = pullParser.getAttributeValue(null, "ref");
                        Long ndId = Long.parseLong(ndIdStr);
                        ((Way) currentPrim).addNode(nodes.get(ndId));
                    } else if ("way".equals(name)) {
                        long id = Long.parseLong(pullParser.getAttributeValue(null, "id"));
                        currentPrim = new Way(id);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if ("node".equals(name)) {
                        nodes.put(currentPrim.getId(), (Node) currentPrim);
                    } else if("way".equals(name)) {
                        ways.put(currentPrim.getId(), (Way) currentPrim);
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                }
                eventType = pullParser.next();
            }

            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        
        Log.i(TAG, "Found " + nodes.size() + " nodes and " + ways.size() + " ways.");
        
        List<OsmPlace> places = new ArrayList<OsmPlace>(ways.size());
        Collection<Way> waysColl = ways.values();
        for (Way way : waysColl) {
            Node firstNode = way.getFirstNode();
            String name = way.getTag("name");
            if(name != null) {
                OsmPlace place = new OsmPlace(name, firstNode.getLocation());
                places.add(place);
            }
        }
        
        Collection<Node> nodesColl = nodes.values();
        for(Node node : nodesColl) {
            String name = node.getTag("name");
            if(name != null) {
                OsmPlace place = new OsmPlace(name, node.getLocation());
                places.add(place);
            }
        }
        
        distanceComparator.currentLocation = params[0];
        Collections.sort(places, distanceComparator);
        
        for (OsmPlace osmPlace : places) {
            if(osmPlace.getName() != null) {
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

        return new StringBuilder(32).append(left).append(",").append(bottom).append(",").append(right).append(",")
                .append(top).toString();
    }

}
