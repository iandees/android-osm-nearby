package com.yellowbkpk.osmnearby.api;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import com.yellowbkpk.osmnearby.model.Changeset;

public class OsmApi {

    private CommonsHttpOAuthConsumer mConsumer;
    private HttpClient mClient;

    public OsmApi(CommonsHttpOAuthConsumer oauth, HttpClient client) {
        this.mConsumer = oauth;
        this.mClient = client;
    }

    public Changeset openChangeset(Map<String, String> tags) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException {
        HttpPut put = new HttpPut("http://openstreetmap.org/api/0.6/changeset/create");
        HttpEntity entity = new StringEntity(buildChangesetPayload(tags));
        put.setEntity(entity);

        this.mConsumer.sign(put);

        String response = this.mClient.execute(put, new BasicResponseHandler());
        long id = Long.parseLong(response);
        return new Changeset(id, tags);
    }

    public void closeChangeset(Changeset changeset) throws ClientProtocolException, IOException,
            OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        HttpPut put = new HttpPut("http://openstreetmap.org/api/0.6/changeset/" + changeset.getId() + "/close");

        this.mConsumer.sign(put);

        this.mClient.execute(put, new BasicResponseHandler());
    }

    private String buildChangesetPayload(Map<String, String> tags) {
        StringBuilder b = new StringBuilder();
        b.append("<osm><changeset>");
        for (Entry<String, String> tag : tags.entrySet()) {
            b.append("<tag k=\"");
            b.append(tag.getKey());
            b.append("\" v=\"");
            b.append(tag.getValue());
            b.append("\" />");
        }
        b.append("</changeset></osm>");
        return b.toString();
    }
}
