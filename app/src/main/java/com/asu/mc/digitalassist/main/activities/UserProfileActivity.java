package com.asu.mc.digitalassist.main.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.firebase.ui.auth.IdpResponse;

public class UserProfileActivity extends AppCompatActivity {

    private static IdpResponse idpResponse = null;

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
                String zip = userProfileZip.getText().toString();

                if (!zip.equalsIgnoreCase("")) {
                    Intent intent = new Intent(v.getContext(), RestaurantActivity.class);
                    intent.putExtra("EXTRA_HOME_ZIP", zip);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Zip code is mandatory!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
