package com.asu.mc.digitalassist.main.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.receivers.RestaurantAlarmReceiver;
import com.firebase.ui.auth.IdpResponse;

import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity {

    private static IdpResponse idpResponse = null;
    private String homeZipCode = null;

    public static Intent createIntent(Context context, IdpResponse response) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        idpResponse = response;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        final EditText userProfileFirstName = (EditText) findViewById(R.id.user_profile_firstname);
        final EditText userProfileLastName = (EditText) findViewById(R.id.user_profile_lastname);
        final EditText userProfileZip = (EditText) findViewById(R.id.user_profile_zip);
        Button saveButton = (Button) findViewById(R.id.user_profile_saveBtn);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String firstName = userProfileFirstName.getText().toString();
                String lastName = userProfileLastName.getText().toString();
                homeZipCode = userProfileZip.getText().toString();

                if (!homeZipCode.equalsIgnoreCase("")) {
                    Intent intent = new Intent(v.getContext(), NavigationActivity.class);
                    scheduleRestaurantSuggestionAlarm();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Zip code is mandatory!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void scheduleRestaurantSuggestionAlarm() {

        Intent intentAlarm = new Intent(this, RestaurantAlarmReceiver.class);
        intentAlarm.putExtra("EXTRA_HOME_ZIP", homeZipCode);

        Calendar lunchTime = Calendar.getInstance();
        lunchTime.set(Calendar.HOUR_OF_DAY, 12);
        lunchTime.set(Calendar.MINUTE, 0);
        lunchTime.set(Calendar.SECOND, 0);

        Calendar dinnerTime = Calendar.getInstance();
        dinnerTime.set(Calendar.HOUR_OF_DAY, 19);
        dinnerTime.set(Calendar.MINUTE, 0);
        dinnerTime.set(Calendar.SECOND, 0);

        // Get the Alarm Service.
        AlarmManager restaurantAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm for a lunch and dinner time.
        restaurantAlarmManager.set(AlarmManager.RTC_WAKEUP, lunchTime.getTimeInMillis(), PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        restaurantAlarmManager.set(AlarmManager.RTC_WAKEUP, dinnerTime.getTimeInMillis(), PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }


}
