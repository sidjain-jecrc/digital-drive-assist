package com.asu.mc.digitalassist.main.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.models.Restaurant;
import com.asu.mc.digitalassist.main.receivers.RestaurantAlarmReceiver;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
                startActivity(new Intent(getApplicationContext(), RestaurantActivity.class));
            }
        });

        btnMoveToSpeedDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Speed tracking started", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                return true;
            case R.id.menu_sign_out:
                userSignOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void userSignOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }
}
