package com.asu.mc.digitalassist.main.activities;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.models.Restaurant;
import com.asu.mc.digitalassist.main.rsclient.RestaurantApiClient;
import com.asu.mc.digitalassist.main.services.GeoLocationTrackerService;
import com.asu.mc.digitalassist.main.utility.Constants;
import com.asu.mc.digitalassist.main.utility.RestaurantListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class RestaurantActivity extends ListActivity implements OnConnectionFailedListener, ConnectionCallbacks, LocationListener {

    protected static final String TAG = RestaurantActivity.class.getSimpleName();

    private static final String ACTION_GEOCODING = "com.asu.mc.digitalassist.activities.services.action.loc.geocode";

    protected GoogleApiClient mGoogleApiClient;
    protected RestaurantApiClient mRestaurantApiClient;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest;

    protected AddressResultReceiver mResultReceiver;
    protected boolean mAddressRequested;
    protected String mCurrentZipCode;

    protected final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResultReceiver = new AddressResultReceiver(new Handler());
        checkAppPermissions();
        buildGoogleApiClient();

        // starting address intent service
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startAddressIntentService();
            mAddressRequested = true;
        }
    }

    protected void startAddressIntentService() {
        Log.d(TAG, "startAddressIntentService");

        Intent intent = new Intent(this, GeoLocationTrackerService.class);
        intent.setAction(ACTION_GEOCODING);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.d(TAG, getString(R.string.restaurant_address_zip_found));
                mCurrentZipCode = resultData.getString(Constants.RESULT_DATA_KEY);
                if (mCurrentZipCode != null) {
                    new FetchRestaurantTask().execute(mCurrentZipCode);
                } else {
                    Log.d(TAG, "Fetched current zip code is null");
                }
            } else {
                Log.d(TAG, getString(R.string.restaurant_address_zip_not_found));
                mCurrentZipCode = getString(R.string.restaurant_address_zip_not_found);
            }
        }
    }

    public void getCurrentKnownLocation(GoogleApiClient mGoogleApiClient) {
        Log.d(TAG, "getCurrentKnownLocation");

        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            Log.e(TAG, "Error: " + e);
        }
    }

    protected void createLocationRequest() {
        Log.d(TAG, "Inside createLocationRequest");

        // displacement takes precedence over time interval
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000); // 1 minute
        mLocationRequest.setFastestInterval(5000); // 5 seconds
        mLocationRequest.setSmallestDisplacement(10); // 10 meters displacement
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    protected void startLocationUpdates() {
        Log.d(TAG, "Inside startLocationUpdates");
        checkAppPermissions();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.e(TAG, "LocationUpdates Error: " + e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Inside onLocationChanged");
        mLastLocation.set(location);
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startAddressIntentService();
        }
    }

    private class FetchRestaurantTask extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... strings) {
            String zipCodeOrCityName = strings[0];
            mRestaurantApiClient = new RestaurantApiClient();
            List<Restaurant> restaurantList = mRestaurantApiClient.getNearbyRestaurantList(zipCodeOrCityName);
            return restaurantList;
        }

        protected void onPostExecute(List<Restaurant> restaurantList) {
            if (restaurantList != null && restaurantList.size() > 0) {
                ArrayAdapter<Restaurant> restaurantArrayAdapter = new RestaurantListAdapter(RestaurantActivity.this, restaurantList);
                setListAdapter(restaurantArrayAdapter);
            } else {
                Toast.makeText(getApplicationContext(), "No restaurants for the current location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            Log.d(TAG, "Building Google API Client");

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void checkAppPermissions() {
        Log.d(TAG, "Inside checkAppPermissions");

        if (ActivityCompat.checkSelfPermission(RestaurantActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "App should show an explanation");
                // TODO: No explanation added yet

            } else {
                Log.d(TAG, "Requesting persmission from user");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION);
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent moveToWebView = new Intent(this, RestaurantWebViewActivity.class);
        Restaurant res = (Restaurant) getListAdapter().getItem(position);
        String url = res.getMobileUrl();
        moveToWebView.putExtra("EXTRA_URL", url);
        startActivity(moveToWebView);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Inside On Start");
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Inside On Destroy");
        super.onDestroy();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Inside On onConnectionFailed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Inside OnConnected");

        checkAppPermissions();
        getCurrentKnownLocation(mGoogleApiClient);

        // in case service was not started earlier, starting it here
        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.restaurant_no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            if (mAddressRequested) {
                Log.d(TAG, "starting address intent service");
                startAddressIntentService();
            }
        }
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Location access permission GRANTED");
                } else {
                    Log.i(TAG, "Location access permission REJECTED");
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Inside On onConnectionSuspended");
        mGoogleApiClient.connect();
    }
}
