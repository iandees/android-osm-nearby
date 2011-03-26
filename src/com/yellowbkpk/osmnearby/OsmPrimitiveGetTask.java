package com.yellowbkpk.osmnearby;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

import com.yellowbkpk.osmnearby.model.OsmData;

public class OsmPrimitiveGetTask extends AsyncTask<Long, Integer, OsmData> {

    private String kind;

    public OsmPrimitiveGetTask(String kind) {
        this.kind = kind;
    }
    
    @Override
    protected OsmData doInBackground(Long... idents) {
        OsmData data = null;
        try {
            URL u = new URL("http://jxapi.openstreetmap.org/xapi/api/0.6/" + kind + "/" + buildIdStr(idents));
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
        return data;
    }

    private String buildIdStr(Long[] idents) {
        if(idents.length == 1) {
            return Long.toString(idents[0]);
        } else {
            StringBuilder b = new StringBuilder();
            for(int i = 0; i < idents.length; i++) {
                b.append(Long.toString(idents[i]));
                if(i != idents.length - 2) {
                    b.append(",");
                }
            }
            return b.toString();
        }
    }
}
