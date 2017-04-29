package com.asu.mc.digitalassist.main.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.models.Restaurant;
import com.asu.mc.digitalassist.main.receivers.RestaurantAlarmReceiver;

import java.util.Calendar;

public class NavigationActivity extends AppCompatActivity {

    protected Button btnMoveToRestaurants;
    protected Button btnMoveToSpeedDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        btnMoveToRestaurants = (Button) findViewById(R.id.navigation_btnRestaurant);
        btnMoveToSpeedDetection = (Button) findViewById(R.id.navigation_btnSpeed);

        btnMoveToRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Restaurant.class));
            }
        });

        btnMoveToSpeedDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Speed tracking started", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
