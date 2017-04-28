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


public class RestaurantRecommendationService extends IntentService implements LocationListener {

    protected static final String TAG = RestaurantRecommendationService.class.getSimpleName();

    private static final int LOCATION_UPDATE_INTERVAL = 60000; // 1 minute
    private static final float LOCATION_UPDATE_DISTANCE = 10f; // 10 meters

    protected String mCurrentZipCode;
    protected String mHomeZip;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;

    private LocationManager mLocationManager;
    private Location currentLocation;

    public RestaurantRecommendationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if (intent != null) {
            mHomeZip = intent.getStringExtra("EXTRA_HOME_ZIP");
            handleLocationTrackerAction();
        }
    }

    public static void startLocationTrackerAction(Context context, String homeZipCode) {
        Log.d(TAG, "startLocationTrackerAction");

        Intent intent = new Intent(context, RestaurantRecommendationService.class);

        //TODO: fetch home zip from SQLite database instead of intent
        intent.putExtra("EXTRA_HOME_ZIP", homeZipCode);

        context.startService(intent);
    }

    public void handleGeoCodingAction() {
        Log.d(TAG, "handleGeoCodingAction");

        String errorMessage = "";
        List<Address> addresses = null;

        if (currentLocation == null) {
            errorMessage = getString(R.string.geocoding_service_no_location_data_provided);
            Log.e(TAG, errorMessage);
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            if (addresses != null || addresses.size() > 0) {
                mCurrentZipCode = addresses.get(0).getPostalCode();

                Log.d(TAG, "Home zip code : " + mHomeZip);
                Log.d(TAG, "Fetched zip code : " + mCurrentZipCode);

                // compare home zip with current zip and start notification service
                if (mHomeZip != null && mCurrentZipCode != null && !mHomeZip.equalsIgnoreCase(mCurrentZipCode)) {
                    Log.d(TAG, "restaurant recommnedation notification");
                    Intent recommendService = NotificationService.createIntentRecommendationNotificationService(RestaurantRecommendationService.this);
                    startService(recommendService);
                } else {
                    Log.d(TAG, "User is in home zip, hence no recommendation");
                }

            } else {
                if (errorMessage.isEmpty()) {
                    errorMessage = "No address found";
                    Log.e(TAG, errorMessage);
                }
            }
        } catch (IOException ioException) {
            errorMessage = getString(R.string.geocoding_service_not_available);
            Log.e(TAG, errorMessage, ioException);

        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.geocoding_service_invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " + "Latitude = " + currentLocation.getLatitude() +
                    ", Longitude = " + currentLocation.getLongitude(), illegalArgumentException);
        }

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            mLocationManager.removeUpdates(RestaurantRecommendationService.this);
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
                        handleGeoCodingAction();
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
                        handleGeoCodingAction();
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
