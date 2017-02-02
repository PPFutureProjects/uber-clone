package com.parse.starter;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;

public class Rider extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    Button btCall;
    Boolean requestActive = false;

    Handler handler = new Handler();
    TextView tvMessage;

    Boolean driverActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);

        redirect();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btCall = (Button) findViewById(R.id.btCall);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Requests");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        requestActive = true;
                        btCall.setText(R.string.cancel_uber);

                        checkForUpdates();
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateMap(lastKnownLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        updateMap(lastKnownLocation);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Rider.class);
        startActivity(intent);
    }

    public void Logout(View view)
    {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Logout Successful",
                            Toast.LENGTH_SHORT
                    ).show();
                    redirect();
                }
            }
        });
    }

    public void redirect()
    {
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void updateMap(Location location)
    {
        if (!driverActive) {
            mMap.clear();
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        }
    }

    public void CallUber(View view)
    {
        if (requestActive) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Requests");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseObject request: objects) {
                                request.deleteInBackground();
                            }
                            requestActive = false;
                            btCall.setText(R.string.call_an_uber);
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Uber Canceled",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
            });
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    ParseObject request = new ParseObject("Requests");
                    request.put("username", ParseUser.getCurrentUser().getUsername());
                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    request.put("location", parseGeoPoint);
                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Request Sent",
                                        Toast.LENGTH_LONG
                                ).show();
                                btCall.setText(R.string.cancel_uber);
                                requestActive = true;

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkForUpdates();
                                    }
                                }, 2000);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Could not find location. Try again later",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }

    public void checkForUpdates()
    {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereExists("driverUsername");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                 if (e == null && objects.size() > 0) {

                     ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                     query1.whereEqualTo("username", objects.get(0).getString("driverUsername"));
                     query1.findInBackground(new FindCallback<ParseUser>() {
                         @Override
                         public void done(List<ParseUser> objects, ParseException e) {
                             if (e == null && objects.size() > 0) {
                                 driverActive = true;

                                 final ParseGeoPoint driverLocation = objects.get(0).getParseGeoPoint("location");

                                 if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                     Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                     if (lastKnowLocation != null) {
                                         ParseGeoPoint userLocation = new ParseGeoPoint(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
                                         Double distanceInKm = driverLocation.distanceInKilometersTo(userLocation);
                                         distanceInKm = (double) Math.round(distanceInKm * 10) / 10;

                                         mMap.clear();
                                         if (distanceInKm < 0.01) {

                                             tvMessage.setText(R.string.driver_arrived);

                                             ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Requests");
                                             query2.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                                             query2.findInBackground(new FindCallback<ParseObject>() {
                                                 @Override
                                                 public void done(List<ParseObject> objects, ParseException e) {
                                                     if (e == null) {
                                                         for (ParseObject request: objects) {
                                                             request.deleteInBackground();
                                                         }
                                                     }
                                                 }
                                             });

                                             handler.postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     tvMessage.setText("");
                                                     btCall.setVisibility(View.VISIBLE);
                                                     btCall.setText(R.string.call_an_uber);
                                                     requestActive = false;
                                                     driverActive = false;

                                                     checkForUpdates();
                                                 }
                                             }, 5000);
                                         }
                                         else {
                                             tvMessage.setText("Your driver is " + distanceInKm.toString() + " km away");

                                             LatLng driverLocationLatLng = new LatLng(
                                                     driverLocation.getLatitude(),
                                                     driverLocation.getLongitude()
                                             );
                                             LatLng requestLocationLatLng = new LatLng(
                                                     userLocation.getLatitude(),
                                                     userLocation.getLongitude()
                                             );

                                             ArrayList<Marker> markers = new ArrayList<Marker>();
                                             markers.add(mMap.addMarker(
                                                     new MarkerOptions().
                                                             position(driverLocationLatLng).
                                                             title("Driver Location").
                                                             icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                                             )));
                                             markers.add(mMap.addMarker(
                                                     new MarkerOptions().
                                                             position(requestLocationLatLng).
                                                             title("Your Location")
                                             ));

                                             LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                             for (Marker marker: markers) {
                                                 builder.include(marker.getPosition());
                                             }
                                             LatLngBounds bounds = builder.build();

                                             int padding = 150;
                                             CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                             mMap.animateCamera(cameraUpdate);

                                             btCall.setVisibility(View.INVISIBLE);
                                             handler.postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     checkForUpdates();
                                                 }
                                             }, 2000);
                                         }
                                     }
                                 }
                             }
                         }
                     });
                 }
            }
        });
    }
}