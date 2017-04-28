package com.asu.mc.digitalassist.main.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.utility.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeoLocationTrackerService extends IntentService implements LocationListener {

    protected static final String TAG = GeoLocationTrackerService.class.getSimpleName();

    private static final String ACTION_TRACK_LOCATION = "com.asu.mc.digitalassist.activities.services.action.loc.track";
    private static final String ACTION_GEOCODING = "com.asu.mc.digitalassist.activities.services.action.loc.geocode";

    private static final int LOCATION_UPDATE_INTERVAL = 60000; // 1 minute
    private static final float LOCATION_UPDATE_DISTANCE = 10f; // 10 meters

    protected String mCurrentZipCode;
    protected String mHomeZip;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;

    private LocationManager mLocationManager;
    private Location currentLocation;
    private AddressResultReceiver mResultReceiver;

    public GeoLocationTrackerService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "<-----------onHandleIntent------------->");

        if (intent != null) {

            final String action = intent.getAction();
            if (ACTION_TRACK_LOCATION.equals(action)) {
                Log.d(TAG, "ACTION_TRACK_LOCATION");

                mResultReceiver = new AddressResultReceiver(new Handler());
                mHomeZip = intent.getStringExtra("EXTRA_HOME_ZIP");
                handleLocationTrackerAction();

            } else if (ACTION_GEOCODING.equals(action)) {
                Log.d(TAG, "ACTION_GEOCODING");

                mResultReceiver = new AddressResultReceiver(new Handler());
                handleGeoCodingAction(intent);
            }
        }
    }

    public static void startLocationTrackerAction(Context context, String homeZipCode) {
        Log.d(TAG, "<------------startLocationTrackerAction------------->");

        Intent intent = new Intent(context, GeoLocationTrackerService.class);
        intent.setAction(ACTION_TRACK_LOCATION);
        intent.putExtra("EXTRA_HOME_ZIP", homeZipCode);
        context.startService(intent);
    }

    protected void startAddressIntentService() {
        Log.d(TAG, "startAddressIntentService");

        Intent intent = new Intent(this, GeoLocationTrackerService.class);
        intent.setAction(ACTION_GEOCODING);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, currentLocation);
        startService(intent);
    }

    public void handleGeoCodingAction(Intent intent) {
        Log.d(TAG, "handleGeoCodingAction");

        String errorMessage = "";
        List<Address> addresses = null;

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if (location == null) {
            errorMessage = getString(R.string.geocoding_service_no_location_data_provided);
            Log.e(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }

        mResultReceiver = (AddressResultReceiver) intent.getParcelableExtra(Constants.RECEIVER);
        if (mResultReceiver == null) {
            Log.e(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null || addresses.size() > 0) {
                String zipResponse = addresses.get(0).getPostalCode();
                Log.d(TAG, "Fetched zip code : " + zipResponse);
                deliverResultToReceiver(Constants.SUCCESS_RESULT, zipResponse);
            } else {
                if (errorMessage.isEmpty()) {
                    errorMessage = "No address found";
                    Log.e(TAG, errorMessage);
                    deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
                }
            }
        } catch (IOException ioException) {
            errorMessage = getString(R.string.geocoding_service_not_available);
            Log.e(TAG, errorMessage, ioException);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);

        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.geocoding_service_invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " + "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }

    }

    public void deliverResultToReceiver(int resultCode, String message) {

        Log.d(TAG, "Inside deliverResultToReceiver");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mResultReceiver.send(resultCode, bundle);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            mLocationManager.removeUpdates(GeoLocationTrackerService.this);
        }
    }

    private void handleLocationTrackerAction() {
        Log.d(TAG, "<-------------handleLocationTrackerAction---------------->");

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        // getting GPS and Network status
        isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled && isNetworkEnabled) {
            try {
                Log.d(TAG, "Network Provider");

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, this);
                if (mLocationManager != null) {
                    currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (currentLocation != null) {
                        startAddressIntentService();
                    } else {
                        Log.d(TAG, "location: " + currentLocation);
                    }
                }

            } catch (java.lang.SecurityException ex) {
                Log.e(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, "network provider does not exist, " + ex.getMessage());
            }

        } else if (isGpsEnabled) {
            try {
                Log.d(TAG, "GPS Provider");

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, this);
                if (mLocationManager != null) {
                    currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (currentLocation != null) {
                        startAddressIntentService();
                    } else {
                        Log.d(TAG, "location: " + currentLocation);
                    }
                }

            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }
        }
    }

    private class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG, "onReceiveResult");

            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.d(TAG, getString(R.string.restaurant_address_zip_found));
                mCurrentZipCode = resultData.getString(Constants.RESULT_DATA_KEY);

                // compare home zip with current zip and start notification service
                if (mHomeZip != null && mCurrentZipCode != null) {
                    NotificationService.createIntentRecommendationNotificationService(getApplicationContext());
                } else {
                    Log.d(TAG, "homeZip: " + mHomeZip + ", currentZip: " + mCurrentZipCode);
                }
            } else {
                Log.d(TAG, getString(R.string.restaurant_address_zip_not_found));
                mCurrentZipCode = getString(R.string.restaurant_address_zip_not_found);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        currentLocation.set(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "onStatusChanged");

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "onProviderEnabled");

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "onProviderDisabled");

    }
}
