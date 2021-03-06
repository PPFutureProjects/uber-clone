package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class DriverRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        redirectUser();

        setTitle("Driver Register");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void RegisterDriver(View view)
    {
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etFullName = (EditText) findViewById(R.id.etFullName);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etCarType = (EditText) findViewById(R.id.etCarType);
        final EditText etLicense = (EditText) findViewById(R.id.etLicense);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);

        if (etPassword.getText().toString().equals(etPassword2.getText().toString()))
        {
            ParseUser user = new ParseUser();
            user.setUsername(etUsername.getText().toString());
            user.setEmail(etEmail.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.put("fullName", etFullName.getText().toString());
            user.put("userType", "Driver");
            user.put("carType", etCarType.getText().toString());
            user.put("licenseNo", etLicense.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                    {
                        Toast.makeText(
                                getApplicationContext(),
                                "Registration Successful",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    else
                    {
                        Toast.makeText(
                                getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(
                    getApplicationContext(),
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void LoginLink(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }

    public void redirectUser()
    {
        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().getString("userType").equals("Rider")) {
                Intent intent = new Intent(getApplicationContext(), Rider.class);
                startActivity(intent);
            }
            else if(ParseUser.getCurrentUser().getString("userType").equals("Driver")) {
                Intent intent = new Intent(getApplicationContext(), DriverViewRequests.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
