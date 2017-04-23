package com.asu.mc.digitalassist.main.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationTrackerService extends IntentService {

    protected static final String TAG = LocationTrackerService.class.getSimpleName();

    private static final String ACTION_LOCATION_TRACK = "com.asu.mc.digitalassist.activities.services.action.loc.track";

    private LocationManager mLocationManager = null;
    private static final int LOCATION_UPDATE_INTERVAL = 60000; // 1 minute
    private static final float LOCATION_UPDATE_DISTANCE = 10f; // 10 meters

    private static String homeZip = null;
    private static final String EXTRA_HOME_ZIP = "com.asu.mc.digitalassist.activities.services.extra.homezip";

    private class LocationTracker implements LocationListener {

        Location mLastLocation;

        public LocationTracker(String provider) {
            Log.d(TAG, "Location instance created");
            this.mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            // TODO: whenever location updates check if the user is still in home zip, if not notify user

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public LocationTrackerService() {
        super(TAG);
    }

    public static void starLocationTrackerAction(Context context, String homeZipCode) {
        Intent intent = new Intent(context, LocationTrackerService.class);
        intent.setAction(ACTION_LOCATION_TRACK);
        intent.putExtra(EXTRA_HOME_ZIP, homeZipCode);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOCATION_TRACK.equals(action)) {
                homeZip = intent.getStringExtra(EXTRA_HOME_ZIP);
                handleLocationTrackerAction();
            }
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationTracker(LocationManager.GPS_PROVIDER),
            new LocationTracker(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.e(TAG, "failed to remove location listeners, ignore", ex);
                }
            }
        }
    }

    private void handleLocationTrackerAction() {

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

}
