/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ViewUtils;
import android.view.View;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redirectUser();
        getSupportActionBar().hide();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void RiderRegisterLink(View view)
    {
        Intent intent = new Intent(getApplicationContext(), RiderRegister.class);
        startActivity(intent);
    }

    public void DriverRegisterLink(View view)
    {
        Intent intent = new Intent(getApplicationContext(), DriverRegister.class);
        startActivity(intent);
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
