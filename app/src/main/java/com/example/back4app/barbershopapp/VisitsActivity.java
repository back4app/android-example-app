package com.example.back4app.barbershopapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitsActivity extends AppCompatActivity {

    List<String> visits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);

        final TextView service = (TextView) findViewById(R.id.service);
        final TextView professional = (TextView) findViewById(R.id.professional);
        final TextView date = (TextView) findViewById(R.id.date);
        final TextView description = (TextView) findViewById(R.id.description);
        final ImageView photo = (ImageView)findViewById(R.id.service_photo);

        /*visits = new ArrayList<>();
        visits.add(getString(R.string.visits));
        final Spinner spinner_visits = (Spinner) findViewById(R.id.visits_dropdown);
        final ArrayAdapter<String> spinner_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, visits);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_visits.setAdapter(spinner_adapter);
        spinner_visits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (!selected.equals(getString(R.string.other_appointments))) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Appointments");
                    query.whereEqualTo("Date", selected);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> visit, ParseException e) {
                            if (e == null) {
                                service.setText(visit.get(0).getString("Service"), TextView.BufferType.EDITABLE);

                                professional.setText(visit.get(0).getString("Professional"), TextView.BufferType.EDITABLE);

                                date.setText(visit.get(0).getString("Date"), TextView.BufferType.EDITABLE);

                                description.setText(visit.get(0).getString("Description"), TextView.BufferType.EDITABLE);

                                ParseFile imageFile = (ParseFile) visit.get(0).getParseFile("Photo");

                                if(imageFile != null){
                                    imageFile.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            photo.setImageBitmap(bitmap);
                                        }
                                    });
                                }

                            } else {
                                Log.d(":(", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.d(":(", "Error");
            }
        });

        String user_name = ParseUser.getCurrentUser().getUsername();

        ParseQuery<ParseObject> query_client_visits = ParseQuery.getQuery("Visits");
        query_client_visits.whereEqualTo("Client", user_name);
        query_client_visits.orderByDescending("updatedAt");
        query_client_visits.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> client_visits, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + client_visits.size() + " visits");
                    for(int i = 0; i < client_visits.size(); i++) {
                        spinner_adapter.add(client_visits.get(i).get("Date").toString());
                    }
                    spinner_adapter.notifyDataSetChanged();
                } else {
                    Log.d(":(", "Error");
                }
            }
        });*/

    }
}
