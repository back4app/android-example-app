package com.example.back4app.barbershopapp;

// Android Dependencies
import android.content.Intent;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Google Maps Dependencies
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Parse Dependencies
import com.parse.FindCallback;
import com.parse.ParseException;

import com.parse.ParseObject;
import com.parse.ParseQuery;

// Java dependencies
import java.util.List;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    List<String> shops_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        showShopInMap(googleMap);

    }

    private void showShopInMap(final GoogleMap googleMap) {

        final TextView name_textView = (TextView) findViewById(R.id.name);
        final TextView address_textView = (TextView) findViewById(R.id.address);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
        query.whereExists("Location");
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> shop, ParseException e) {
                if (e == null && shop.size() > 0) {

                    name_textView.setText(shop.get(0).getString("Name"), TextView.BufferType.EDITABLE);
                    String address = shop.get(0).getNumber("Number").toString() + " " + shop.get(0).getString("Address") + ", " + shop.get(0).getString("City") + ", " + shop.get(0).getString("State") + ", " + shop.get(0).getString("Country");
                    address_textView.setText(address , TextView.BufferType.EDITABLE);

                    LatLng shopLocation = new LatLng(shop.get(0).getParseGeoPoint("Location").getLatitude(), shop.get(0).getParseGeoPoint("Location").getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(shopLocation).title(shop.get(0).getString("Name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    // zoom the map to the shop
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(shopLocation, 10));

                }
            }
        });
        ParseQuery.clearAllCachedResults();

    }

}