package com.example.back4app.barbershopapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduledActivity extends AppCompatActivity {

    List<String> services_list = new ArrayList<>();
    List<String> dates_list = new ArrayList<>();
    List<String> appointments_services_copy = new ArrayList<>();
    ParseObject objectOnTheScreen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled);

        final TextView services_textview = (TextView) findViewById(R.id.services_textview);
        final TextView professionals_textview = (TextView) findViewById(R.id.professionals_textview);
        final TextView dates_textview = (TextView) findViewById(R.id.dates_textview);
        final TextView time_textview = (TextView) findViewById(R.id.time_textview);
        final List<ParseObject> client_appointments = new ArrayList<>();

        final Button back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle = new Bundle();
                final Intent intent = new Intent(ScheduledActivity.this, MenuActivity.class);
                bundle.putString("TabNumber", "1");
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final Button another_appointment_button = findViewById(R.id.add_button);
        another_appointment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduledActivity.this, SchedulingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final Button delete_button = findViewById(R.id.button_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(services_textview.length() > 0) {
                    alertDisplayerToDelete(getString(R.string.warning), getString(R.string.really_want_to_delete), services_textview.getText().toString(), professionals_textview.getText().toString(), dates_textview.getText().toString(), time_textview.getText().toString(), objectOnTheScreen);
                }
                else{
                    Toast.makeText(ScheduledActivity.this, getString(R.string.select_appointment),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        // services dropdown
        services_list.add(getString(R.string.other_scheduled_services));
        final Spinner spinner_service = (Spinner) findViewById(R.id.services_dropdown);
        final ArrayAdapter<String> spinner_service_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, services_list);
        spinner_service_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_service.setAdapter(spinner_service_adapter);

        // dates dropdown
        dates_list.add(getString(R.string.select_date));
        final Spinner spinner_dates = (Spinner) findViewById(R.id.dates_dropdown);
        final ArrayAdapter<String> spinner_dates_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates_list);
        spinner_dates_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_dates.setAdapter(spinner_dates_adapter);

        if(getIntent().getExtras().getString("service") != null) {
                services_textview.setText(getIntent().getExtras().getString("service"));
                professionals_textview.setText(getIntent().getExtras().getString("professional"));
                dates_textview.setText(getIntent().getExtras().getString("date"));
                time_textview.setText(getIntent().getExtras().getString("time"));
        }

        ParseQuery<ParseObject> query_appointments = ParseQuery.getQuery("Appointments");
        query_appointments.whereEqualTo("Client", ParseUser.getCurrentUser().getUsername());
        query_appointments.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> appointments, ParseException e) {
                if (e == null && appointments.size() > 0) {
                    client_appointments.addAll(appointments);

                    int i;
                    String service;
                    appointments_services_copy.clear();

                    for(i = 0; i < appointments.size(); i++)
                        appointments_services_copy.add(appointments.get(i).getString("Appointment_Service"));

                   while(appointments_services_copy.size() > 0) {
                        service = appointments_services_copy.get(0);
                        spinner_service_adapter.add(service);
                        appointments_services_copy.removeAll(Collections.singleton(service));
                    }
                    spinner_service_adapter.notifyDataSetChanged();
                } else {
                    Log.d("oh, oh...", "error");
                }
            }
        });

        spinner_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent_service, View view_service, int position_service, long id_service) {
                String selected_service = parent_service.getItemAtPosition(position_service).toString();
                if (!selected_service.equals(R.string.other_scheduled_services)) {
                    spinner_dates_adapter.clear();
                    dates_list.add(getString(R.string.scheduled_dates));
                    spinner_dates_adapter.notifyDataSetChanged();

                    for(int i = 0; i < client_appointments.size(); i++) {
                        if(client_appointments.get(i).getString("Appointment_Service").equals(selected_service)){

                                spinner_dates_adapter.add(client_appointments.get(i).getString("Appointment_Date"));
                        }
                        spinner_dates_adapter.notifyDataSetChanged();

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent_service) {

            }
        });

        spinner_dates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent_service, View view_service, int position_service, long id_service) {
                String selected_date = parent_service.getItemAtPosition(position_service).toString();
                if (!selected_date.equals(R.string.scheduled_dates)) {
                    for(int i = 0; i < client_appointments.size(); i++) {
                        if(client_appointments.get(i).getString("Appointment_Service").equals(spinner_service.getSelectedItem().toString()) && client_appointments.get(i).getString("Appointment_Date").equals(spinner_dates.getSelectedItem().toString())){

                            objectOnTheScreen = client_appointments.get(i);

                            services_textview.setText(client_appointments.get(i).getString("Appointment_Professional"));
                            professionals_textview.setText(client_appointments.get(i).getString("Appointment_Service"));
                            dates_textview.setText(client_appointments.get(i).getString("Appointment_Date"));
                            time_textview.setText(client_appointments.get(i).getString("Appointment_Time"));
                        }
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent_service) {

            }
        });

    }

    private void alertDisplayerToDelete(String title, String message, final String service, final String professional, final String date, final String time, final ParseObject object){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScheduledActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_yes_no, null);
        final TextView title_textview = (TextView) mView.findViewById(R.id.title);
        final TextView message_textview = (TextView) mView.findViewById(R.id.message);
        Button mConfirm = (Button) mView.findViewById(R.id.confirm);
        Button mCancel = (Button) mView.findViewById(R.id.cancel);

        title_textview.setText(title);
        message_textview.setText(message);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final Bundle bundle = new Bundle();
                final Intent intent = new Intent(ScheduledActivity.this, MenuActivity.class);
                bundle.putString("TabNumber", "1");
                intent.putExtras(bundle);


                ParseQuery<ParseObject> query_professional_schedule = ParseQuery.getQuery("Professionals_Schedule");
                query_professional_schedule.whereEqualTo("Name", professional);
                query_professional_schedule.whereEqualTo("Date", date);
                query_professional_schedule.setLimit(1);
                query_professional_schedule.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> professionals_schedule_info, ParseException e) {
                        if (e == null && professionals_schedule_info.size() > 0) {
                            List<String> alreadyScheduled = new ArrayList<>();
                            alreadyScheduled = professionals_schedule_info.get(0).getList("Already_Scheduled");
                            alreadyScheduled.remove(time);
                            professionals_schedule_info.get(0).put("Already_Scheduled", alreadyScheduled);
                            professionals_schedule_info.get(0).saveInBackground();
                        } else {
                            Log.d(":(", "Error");
                        }
                    }
                });

                if(object != null) {
                    object.deleteInBackground();
                }
                else{
                    ParseQuery<ParseObject> query_object = ParseQuery.getQuery("Appointments");
                    query_object.whereEqualTo("Appointment_Service", service);
                    query_object.whereEqualTo("Appointment_Professional", professional);
                    query_object.whereEqualTo("Appointment_Date", date);
                    query_object.whereEqualTo("Appointment_Time", time);
                    query_object.setLimit(1);
                    query_object.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null & objects.size() > 0) {
                                objects.get(0).deleteInBackground();

                            } else {
                                Intent intent = new Intent(ScheduledActivity.this, ScheduledActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("service", service);
                                intent.putExtra("professional", professional);
                                intent.putExtra("date", date);
                                intent.putExtra("time", time);
                                intent.putExtra("object", object);
                                Toast.makeText(ScheduledActivity.this, getString(R.string.not_deleted_appointment), Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        }
                    });

                }

                Toast.makeText(ScheduledActivity.this, getString(R.string.deleted_appointment), Toast.LENGTH_LONG).show();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.cancel();
                Toast.makeText(ScheduledActivity.this, getString(R.string.not_deleted_appointment), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed () {
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(ScheduledActivity.this, MenuActivity.class);
        bundle.putString("TabNumber", "1");
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
