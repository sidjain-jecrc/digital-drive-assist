package com.asu.mc.digitalassist.main.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.models.User;
import com.asu.mc.digitalassist.main.receivers.RestaurantAlarmReceiver;
import com.asu.mc.digitalassist.main.utility.UserProfileDbHelper;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity {

    private static IdpResponse idpResponse = null;
    private String homeZipCode = null;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private UserProfileDbHelper dbHelper = null;
    private static final String DB_NAME = "UserDB";
    private static final String TABLE_NAME = "UserProfile";
    public static Intent createIntent(Context context, IdpResponse response) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        idpResponse = response;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getApplicationContext().deleteDatabase(DB_NAME);
        dbHelper = new UserProfileDbHelper(this,DB_NAME);
        final EditText userProfileFirstName = (EditText) findViewById(R.id.user_profile_firstname);
        final EditText userProfileLastName = (EditText) findViewById(R.id.user_profile_lastname);
        final EditText userProfileZip = (EditText) findViewById(R.id.user_profile_zip);
        String fullName = firebaseAuth.getCurrentUser().getDisplayName();
        if(fullName!=null){
            String[] names = fullName.split(" ");
            String firstName = null;
            String lastName = null;
            if(names.length==1){
                firstName = names[0];
            }
            else{
                firstName = names[0];
                lastName = names[1];
            }
            userProfileFirstName.setText(firstName);
            userProfileLastName.setText(lastName);
        }

        Button saveButton = (Button) findViewById(R.id.user_profile_saveBtn);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String firstName = userProfileFirstName.getText().toString();
                String lastName = userProfileLastName.getText().toString();
                homeZipCode = userProfileZip.getText().toString();
                String email = firebaseAuth.getCurrentUser().getEmail();

                if (!homeZipCode.equalsIgnoreCase("")) {
                    User user = new User(firstName,lastName,email,Long.parseLong(homeZipCode));
                    dbHelper.createTable(TABLE_NAME);
                    dbHelper.addUserToDB(user,TABLE_NAME);
                   //Uncomment following to test database
                    /* ArrayList<User> userList = (ArrayList<User>) dbHelper.getUsersFromDB(TABLE_NAME,"");
                    for(User u:userList){
                        Log.e("fname: ",u.getFirstName());
                        Log.e("lname: ",u.getLastName());
                        Log.e("email: ",u.getEmail());
                        Log.e("zip: ",String.valueOf(u.getZip()));
                    }*/
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
