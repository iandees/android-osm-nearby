package com.yellowbkpk.osmnearby;

import com.yellowbkpk.osmnearby.model.OsmData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class PlaceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        String kind = extras.getString("kind");
        Long id = extras.getLong("id");
        
        new OsmPrimitiveGetTask(kind) {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(PlaceActivity.this, "", "Loading data...");
            }

            @Override
            protected void onPostExecute(OsmData result) {
                
                dialog.dismiss();
            }
        }.execute(id);
    }

}
