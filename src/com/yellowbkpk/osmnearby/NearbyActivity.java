package com.yellowbkpk.osmnearby;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NearbyActivity extends ListActivity implements LocationListener {
    private static final long ONE_MINUTE = 60 * 1000;
    private static final String TAG = NearbyActivity.class.getName();
    private ArrayAdapter<OsmPlace> adapter;
    private Location currentBestLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        adapter = new ArrayAdapter<OsmPlace>(this, R.layout.location_list_item, R.id.location_list_item_name) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                
                OsmPlace item = adapter.getItem(position);
                TextView distView = (TextView) v.findViewById(R.id.location_list_item_distance);
                distView.setText(item.getLoc().distanceTo(currentBestLocation) + " meters");
                
                return v;
            }
        };
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        startGPS();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        OsmPlace item = adapter.getItem(position);
        
        Log.i(TAG, "Touched item " + item.getName());
    }

    private void startGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_MINUTE, 100, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, ONE_MINUTE, 100, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        stopGPS();
    }

    private void stopGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "New location: " + location);
        if (isBetterLocation(location, currentBestLocation)) {
            currentBestLocation = location;
            Log.i(TAG, "Best location: " + currentBestLocation);
            new GetOsmPlacesTask() {
                @Override
                protected void onPostExecute(List<OsmPlace> result) {
                    adapter.clear();
                    for (OsmPlace osmPlace : result) {
                        adapter.add(osmPlace);
                    }
                    adapter.notifyDataSetChanged();
                }
            }.execute(currentBestLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}