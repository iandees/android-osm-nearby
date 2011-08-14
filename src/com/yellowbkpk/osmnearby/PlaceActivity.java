package com.yellowbkpk.osmnearby;

import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.yellowbkpk.osmnearby.model.OsmData;
import com.yellowbkpk.osmnearby.model.Primitive;

public class PlaceActivity extends Activity {
    private Primitive selectedPrimitive;
    
    private OnClickListener addTagListener = new AddTagListener();
    private OnClickListener editTagListener = new EditTagListener();
    
    private class AddTagListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            View editView = getLayoutInflater().inflate(R.layout.dialog_add_tag, null);
            
            final EditText keyView = (EditText) editView.findViewById(R.id.key_view);
            
            final EditText valueView = (EditText) editView.findViewById(R.id.value_view);

            new AlertDialog.Builder(PlaceActivity.this).setView(editView).setTitle("Add New Tag")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedPrimitive.setTag(keyView.getText().toString(), valueView.getText().toString());
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
    
    private class EditTagListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final Entry<String, String> tag = (Entry<String, String>) v.getTag();

            View editView = getLayoutInflater().inflate(R.layout.dialog_edit_tag, null);
            
            final EditText valueView = (EditText) editView.findViewById(R.id.value_view);
            valueView.setText(tag.getValue());

            new AlertDialog.Builder(PlaceActivity.this).setView(editView).setTitle("Edit tag \"" + tag.getKey() + "\"")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedPrimitive.setTag(tag.getKey(), valueView.getText().toString());
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

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
        
        for (Entry<String, String> tag : selectedPrimitive.getTags().entrySet()) {
            TableRow view = createAttributeRow(tag);
            container.addView(view);
        }
        container.addView(createAttributeRow(null));

    }

    private TableRow createAttributeRow(Entry<String,String> tag) {
        TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.place_attribute_row, null);
        
        Button buttonView = (Button) row.findViewById(R.id.attribute_text_button);
        TextView labelView = (TextView) row.findViewById(R.id.attribute_label);
        
        if (tag == null) {
            labelView.setText("<New Tag>");
            buttonView.setText("Not Set Yet");
            buttonView.setOnClickListener(addTagListener);
        } else {
            labelView.setText(tag.getKey());
            
            buttonView.setText(tag.getValue());
            buttonView.setOnClickListener(editTagListener);
            buttonView.setTag(tag);
        }
        
        return row;
    }

}
