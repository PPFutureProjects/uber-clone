package com.parse.starter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        intent = getIntent();
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
}
