package com.parse.starter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        RelativeLayout activity_driver_map = (RelativeLayout) findViewById(R.id.activity_driver_map);
        activity_driver_map.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LatLng driverLocation = new LatLng(
                        intent.getDoubleExtra("driverLatitude", 0),
                        intent.getDoubleExtra("driverLongitude", 0)
                );
                LatLng requestLocation = new LatLng(
                        intent.getDoubleExtra("requestLatitude", 0),
                        intent.getDoubleExtra("requestLongitude", 0)
                );

                ArrayList<Marker> markers = new ArrayList<Marker>();
                markers.add(mMap.addMarker(
                        new MarkerOptions().position(driverLocation).title("My Location")));
                markers.add(mMap.addMarker(
                        new MarkerOptions().position(requestLocation).title("Request Location")
                ));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker: markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                int padding = 150;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                mMap.animateCamera(cameraUpdate);
            }
        });
    }

    public void AcceptRequest(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        query.whereEqualTo("username", intent.getStringExtra("username"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject request: objects) {
                            request.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                            request.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //ParseUser.logOut();
                                        Intent directionIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr=" +
                                                        intent.getDoubleExtra("driverLatitude", 0) + ","
                                                        + intent.getDoubleExtra("driverLongitude", 0) +
                                                        "&daddr=" + intent.getDoubleExtra("requestLatitude", 0)
                                                        + "," + intent.getDoubleExtra("requestLongitude", 0)));
                                        startActivity(directionIntent);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
