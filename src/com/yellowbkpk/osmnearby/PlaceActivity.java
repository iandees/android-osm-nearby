package com.yellowbkpk.osmnearby;

import com.yellowbkpk.osmnearby.model.OsmData;
import com.yellowbkpk.osmnearby.model.Primitive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceActivity extends Activity {
    private Primitive selectedPrimitive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        String kind = extras.getString("kind");
        Long id = extras.getLong("id");
        
        setContentView(R.layout.place);
        
        new OsmPrimitiveGetTask(kind) {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(PlaceActivity.this, "", "Loading data...");
            }

            @Override
            protected void onPostExecute(OsmData result) {
                selectedPrimitive = findPrimitive(result);
                updateUI(result);
                
                dialog.dismiss();
            }
        }.execute(id);
    }

    private Primitive findPrimitive(OsmData result) {
        if(result.getWays().size() > 0) {
            return result.getWays().values().iterator().next();
        } else {
            return result.getNodes().values().iterator().next();
        }
    }

    private void updateUI(OsmData result) {
        ImageView iconview = (ImageView) findViewById(R.id.place_view_icon);
        iconview.setImageResource(IconLookup.forTags(selectedPrimitive.getTags()));

        TextView nameView = (TextView) findViewById(R.id.place_view_name);
        nameView.setText(selectedPrimitive.getTag("name"));
    }

}
