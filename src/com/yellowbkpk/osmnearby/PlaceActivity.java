package com.yellowbkpk.osmnearby;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.yellowbkpk.osmnearby.model.OsmData;
import com.yellowbkpk.osmnearby.model.Primitive;

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
        if (result.getWays().size() > 0) {
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
        
        TableLayout container = (TableLayout) findViewById(R.id.attribute_table);
        
        if (selectedPrimitive.getTag("addr:full") != null) {
            container.addView(createAttributeRow("Address:", selectedPrimitive.getTag("addr:full")));
        }

        if (selectedPrimitive.getTag("phone") != null) {
            container.addView(createAttributeRow("Phone:", selectedPrimitive.getTag("phone")));
        }

        if (selectedPrimitive.getTag("opening_hours") != null) {
            container.addView(createAttributeRow("Hours:", selectedPrimitive.getTag("opening_hours")));
        }

    }

    private TableRow createAttributeRow(String label, String value) {
        TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.place_attribute_row, null);
        
        TextView labelView = (TextView) row.findViewById(R.id.attribute_label);
        labelView.setText(label);
        
        Button buttonView = (Button) row.findViewById(R.id.attribute_text_button);
        buttonView.setText(value);
        
        return row;
    }

}
