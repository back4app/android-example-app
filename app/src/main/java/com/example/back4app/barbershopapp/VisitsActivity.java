package com.example.back4app.barbershopapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.nullphoto);
        photo.setImageBitmap(bitmap);

        final Button back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                final Intent intent = new Intent(VisitsActivity.this, MenuActivity.class);
                bundle.putString("TabNumber", "1");
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        visits = new ArrayList<>();
        visits.add(getString(R.string.visits));
        final Spinner spinner_visits = (Spinner) findViewById(R.id.visits_dropdown);
        final ArrayAdapter<String> spinner_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, visits);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_visits.setAdapter(spinner_adapter);
        spinner_visits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (!selected.equals(getString(R.string.visits))) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Visits");
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
                                else{
                                    photo.setImageBitmap(bitmap);

                                }

                            } else {
                                Log.d(":(", "Error: " + e.getMessage());
                                photo.setImageBitmap(bitmap);
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

        ParseQuery<ParseObject> query_client_visits = ParseQuery.getQuery("Visits");
        query_client_visits.whereEqualTo("Client", ParseUser.getCurrentUser().getUsername());
        query_client_visits.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> client_visits, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < client_visits.size(); i++) {
                        spinner_adapter.add(client_visits.get(i).get("Date").toString());
                    }
                    spinner_adapter.notifyDataSetChanged();
                } else {
                    Log.d(":(", "Error");
                }
            }
        });

    }
}
