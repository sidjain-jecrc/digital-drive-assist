package com.asu.mc.digitalassist.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.activities.utility.LocationUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements OnConnectionFailedListener, ConnectionCallbacks {

    protected static final String TAG = MainActivity.class.getSimpleName();

    protected GoogleApiClient mGoogleApiClient;

    protected TextView txtLat;
    protected TextView txtLong;

    protected final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLat = (TextView) findViewById(R.id.txtGpsLatitude);
        txtLong = (TextView) findViewById(R.id.txtGpsLongitude);

        checkAppPermissions();
        buildGoogleApiClient();
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

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
        String latLong = LocationUtility.getCurrentKnownLocation(mGoogleApiClient);
        String[] coordinates = latLong.split(",");
        txtLat.setText(coordinates[0]);
        txtLong.setText(coordinates[1]);
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
