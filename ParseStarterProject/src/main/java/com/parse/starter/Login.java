package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        redirectUser();

        setTitle("Login");
    }

    public void LoginUser(View view)
    {
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        ParseUser.logInInBackground(
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null)
                        {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                            ).show();
                            redirectUser();
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
                }
        );
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
        }
    }
}
