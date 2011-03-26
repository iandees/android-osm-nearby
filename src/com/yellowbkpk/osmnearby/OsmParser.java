package com.yellowbkpk.osmnearby;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.location.Location;

import com.yellowbkpk.osmnearby.model.Node;
import com.yellowbkpk.osmnearby.model.OsmData;
import com.yellowbkpk.osmnearby.model.Primitive;
import com.yellowbkpk.osmnearby.model.Way;

public class OsmParser {

    private Primitive currentPrim;
    private Map<Long, Node> nodes = new HashMap<Long, Node>();
    private Map<Long, Way> ways = new HashMap<Long, Way>();

    public OsmData parse(InputStream inputStream) {
        try {
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
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new OsmData(nodes, ways);
    }

}
