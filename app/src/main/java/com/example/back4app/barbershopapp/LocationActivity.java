package com.example.back4app.barbershopapp;

// Android Dependencies
import android.app.FragmentManager;
import android.content.Intent;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Arrays;
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
        mMap = googleMap;

        // services dropdown
        shops_list = new ArrayList<>();
        shops_list.add(getString(R.string.select_shop));
        final Spinner spinner_shops = (Spinner) findViewById(R.id.shop_dropdown);
        final ArrayAdapter<String> spinner_shops_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shops_list);
        spinner_shops_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_shops.setAdapter(spinner_shops_adapter);
        spinner_shops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent_service, View view_service, int position_service, long id_service) {
                String selected_shop = parent_service.getItemAtPosition(position_service).toString();
                if (!selected_shop.equals(R.string.select_service)) {
                    showShopInMap(googleMap, selected_shop);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent_service) {
                Log.d("oh, oh...", "Error");
            }
        });

        ParseQuery<ParseObject> query_services = ParseQuery.getQuery("Location");
        query_services.whereExists("Location");
        query_services.selectKeys(Arrays.asList("Name"));
        query_services.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> shops, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < shops.size(); i++) {
                        spinner_shops_adapter.add(shops.get(i).get("Name").toString());
                    }
                    spinner_shops_adapter.notifyDataSetChanged();
                } else {
                    Log.d("oh, oh...", "error");
                }
            }
        });
    }

    private void showShopInMap(final GoogleMap googleMap, String name) {

        if (!name.equals("default")) {

            setContentView(R.layout.activity_location);

            final TextView name_textView = (TextView) findViewById(R.id.name);
            final TextView address_textView = (TextView) findViewById(R.id.address);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
            query.whereEqualTo("Name", name);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> shop, ParseException e) {
                    if (e == null) {

                        name_textView.setText(shop.get(0).getString("Name"), TextView.BufferType.EDITABLE);
                        String address = shop.get(0).getNumber("Number").toString() + " " + shop.get(0).getString("Address") + ", " + shop.get(0).getString("City") + ", " + shop.get(0).getString("State") + ", " + shop.get(0).getString("Country");
                        address_textView.setText(address , TextView.BufferType.EDITABLE);

//                        LatLng shopLocation = new LatLng(shop.get(0).getParseGeoPoint("Location").getLatitude(), shop.get(0).getParseGeoPoint("Location").getLongitude());
//                        googleMap.addMarker(new MarkerOptions().position(shopLocation).title(shop.get(0).getString("Name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//
//                        // zoom the map to the shop
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(shopLocation, 10));

                    } else {
                        // handle the error
                        Log.d("store", "Error: " + e.getMessage());
                    }
                }
            });
            ParseQuery.clearAllCachedResults();
        }
    }

}