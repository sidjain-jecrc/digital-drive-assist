package com.asu.mc.digitalassist.main.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.models.User;
import com.asu.mc.digitalassist.main.utility.UserProfileDbHelper;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText editText_oldPwd = null;
    EditText editText_newPwd = null;
    EditText editText_confirmPwd = null;
    TextView textView_msg = null;
    private static IdpResponse idpResponse = null;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Button update_Button = null;
    private UserProfileDbHelper dbHelper = null;
    private static final String DB_NAME = "UserDB";
    private static final String TABLE_NAME = "UserProfile";

    public static Intent createIntent(Context context, IdpResponse response) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        idpResponse = response;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = new UserProfileDbHelper(this, DB_NAME);

        textView_msg = (TextView) findViewById(R.id.textView_msg);

        update_Button = (Button) findViewById(R.id.button_pwdUpdate);
        update_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText_oldPwd = (EditText) findViewById(R.id.editText_oldPwd);
                editText_newPwd = (EditText) findViewById(R.id.editText_newPwd);
                editText_confirmPwd = (EditText) findViewById(R.id.editText_confirmPwd);
                String oldPassword = editText_oldPwd.getText().toString();
                final String newPassword = editText_newPwd.getText().toString();
                String confirmPassword = editText_confirmPwd.getText().toString();

                if (oldPassword.equals("") || newPassword.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();

                } else if (newPassword.equals(confirmPassword)) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    User existingUser = dbHelper.getUserDB(TABLE_NAME, user.getEmail());
                    String provider = existingUser.getProvider();

                    if (provider.equals("password")) {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Log.e("Reuthenticate: ", "User re-authenticated.");
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Log.e("PASSWORD Update:", "User password updated.");
                                                textView_msg.setText("Password updated successfully");
                                                Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ChangePasswordActivity.this, NavigationActivity.class));
                                            } else {
                                                Log.e("PASSWORD Update:", "User password not updated.");
                                                textView_msg.setText("Password not updated");
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("Reuthenticate: ", "User re-authenticate failed.");
                                    textView_msg.setText("Invalid credentials");
                                }
                            }
                        });
                    }
                } else {
                    textView_msg.setText("New passwords did not match");
                }
            }
        });

    }
}
