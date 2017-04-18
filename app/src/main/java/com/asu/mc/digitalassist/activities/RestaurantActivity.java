package com.asu.mc.digitalassist.activities;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.activities.models.Restaurant;
import com.asu.mc.digitalassist.activities.rsclient.RestaurantApiClient;
import com.asu.mc.digitalassist.activities.utility.LocationUtility;
import com.asu.mc.digitalassist.activities.utility.RestaurantListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class RestaurantActivity extends ListActivity implements OnConnectionFailedListener, ConnectionCallbacks {

    protected static final String TAG = RestaurantActivity.class.getSimpleName();

    protected GoogleApiClient mGoogleApiClient;
    protected RestaurantApiClient mRestaurantApiClient;

    protected TextView txtLat;
    protected TextView txtLong;
    protected TextView txtRestName;

    protected final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        txtLat = (TextView) findViewById(R.id.txtGpsLatitude);
//        txtLong = (TextView) findViewById(R.id.txtGpsLongitude);
//        txtRestName = (TextView) findViewById(R.id.txtRestaurantName);

//        checkAppPermissions();
//        buildGoogleApiClient();

        new FetchRestaurantTask().execute("85281");

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
            if (restaurantList.size() > 0) {
                setListAdapter(new RestaurantListAdapter(RestaurantActivity.this, restaurantList));
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

//        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Inside On Destroy");
        super.onDestroy();

//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Inside On onConnectionFailed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Inside OnConnected");

//        checkAppPermissions();
//        String latLong = LocationUtility.getCurrentKnownLocation(mGoogleApiClient);
//        String[] coordinates = latLong.split(",");
//        txtLat.setText(coordinates[0]);
//        txtLong.setText(coordinates[1]);
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
//        mGoogleApiClient.connect();
    }
}
