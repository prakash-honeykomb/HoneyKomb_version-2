package com.honeykomb.honeykomb.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.honeykomb.honeykomb.R;

import java.util.ArrayList;

public class EventLocationMapView extends AppCompatActivity {
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_location_view);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        final ArrayList<String> details = bundle.getStringArrayList("eventDetails");
        if (details != null && details.size() > 23 && details.get(23).length() > 2) {
            String[] latLong = details.get(23).split(",");
            final String latitude = latLong[0];
            final String longitude = latLong[1];
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    if (latitude != null) {
                        LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        googleMap.addMarker(new MarkerOptions().position(location).title(details.get(1)));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
                        googleMap.animateCamera(cameraUpdate);
                    }
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}

