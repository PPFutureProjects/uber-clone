package com.parse.starter;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.vision.barcode.Barcode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DriverViewRequests extends AppCompatActivity {

    private ListView lvRequests;
    ArrayList<String> requests = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    ArrayList<Double> requestLatitudes = new ArrayList<Double>();
    ArrayList<Double> requestLongitudes = new ArrayList<Double>();
    ArrayList<String> usernames = new ArrayList<String>();

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_requests);

        lvRequests = (ListView) findViewById(R.id.lvRequests);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
        requests.clear();
        requests.add("Getting Nearby Requests. Please Wait");
        lvRequests.setAdapter(arrayAdapter);

        lvRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (requestLatitudes.size() > position &&requestLongitudes.size() > position && usernames.size() > position && lastKnownLocation != null) {
                        Intent intent = new Intent(getApplicationContext(), DriverMap.class);
                        intent.putExtra("requestLatitude", requestLatitudes.get(position));
                        intent.putExtra("requestLongitude", requestLongitudes.get(position));
                        intent.putExtra("driverLatitude", lastKnownLocation.getLatitude());
                        intent.putExtra("driverLongitude", lastKnownLocation.getLongitude());
                        intent.putExtra("username", usernames.get(position));
                        startActivity(intent);
                    }
                }
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateListViewMethod(location);
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateListViewMethod(lastKnownLocation);
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
                        updateListViewMethod(lastKnownLocation);
                    }
                }
            }
        }
    }

    public void updateListViewMethod(Location location)
    {
        if (location != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
            final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            query.whereNear("location", geoPointLocation);
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        requests.clear();
                        requestLatitudes.clear();
                        requestLongitudes.clear();

                        if (objects.size() > 0) {
                            for (ParseObject request: objects) {
                                ParseGeoPoint requestLocation = (ParseGeoPoint) request.get("location");

                                Double distanceInKm = geoPointLocation.distanceInKilometersTo(requestLocation);
                                distanceInKm = (double) Math.round(distanceInKm * 10) /10;
                                requests.add(distanceInKm.toString() + " km");

                                requestLatitudes.add(requestLocation.getLatitude());
                                requestLongitudes.add(requestLocation.getLongitude());
                                usernames.add(request.getString("username"));
                            }
                        } else {
                            requests.add("No active requests nearby");
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
