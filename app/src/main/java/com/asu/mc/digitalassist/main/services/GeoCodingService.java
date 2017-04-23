package com.asu.mc.digitalassist.main.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.utility.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoCodingService extends IntentService {

    protected static final String TAG = GeoCodingService.class.getSimpleName();

    protected static ResultReceiver mReceiver;
    public GeoCodingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String errorMessage = "";
        List<Address> addresses = null;

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if (location == null) {
            errorMessage = getString(R.string.geocoding_service_no_location_data_provided);
            Log.e(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if (mReceiver == null) {
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

    public static void deliverResultToReceiver(int resultCode, String message) {
        Log.d(TAG, "Inside deliverResultToReceiver");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}
