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
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulingActivity extends AppCompatActivity {

    List<String> services_list = new ArrayList<>();
    List<String> professionals_list = new ArrayList<>();
    List<String> dates_list = new ArrayList<>();
    List<String> time_list = new ArrayList<>();
    ParseObject alreadyScheduled;
    List<String> new_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling);
        Parse.initialize(this);

        final TextView price_textview = (TextView) findViewById(R.id.price);
        final List<ParseObject> services_objects = new ArrayList<>();
        final List<ParseObject> professionals_schedule_objects = new ArrayList<>();



        final ImageView back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchedulingActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // services dropdown
        services_list.add(getString(R.string.select_service));
        final Spinner spinner_service = (Spinner) findViewById(R.id.services_dropdown);
        final ArrayAdapter<String> spinner_service_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, services_list);
        spinner_service_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_service.setAdapter(spinner_service_adapter);

        // professionals dropdown
        professionals_list.add(getString(R.string.select_professional));
        final Spinner spinner_professionals = (Spinner) findViewById(R.id.professionals_dropdown);
        final ArrayAdapter<String> spinner_professionals_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, professionals_list);
        spinner_professionals_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_professionals.setAdapter(spinner_professionals_adapter);

        // dates dropdown
        dates_list.add(getString(R.string.select_date));
        final Spinner spinner_dates = (Spinner) findViewById(R.id.dates_dropdown);
        final ArrayAdapter<String> spinner_dates_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates_list);
        spinner_dates_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_dates.setAdapter(spinner_dates_adapter);

        // time dropdown
        time_list.add(getString(R.string.select_time));
        final Spinner spinner_time = (Spinner) findViewById(R.id.time_dropdown);
        final ArrayAdapter<String> spinner_time_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, time_list);
        spinner_time_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_time.setAdapter(spinner_time_adapter);


        ParseQuery<ParseObject> query_services = ParseQuery.getQuery("Services");
        query_services.whereExists("Type");
        query_services.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> services, ParseException e) {
                if (e == null && services.size() > 0) {
                    services_objects.addAll(services);

                    for(int i = 0; i < services.size(); i++) {
                        spinner_service_adapter.add(services.get(i).get("Type").toString());
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
                if (!selected_service.equals(R.string.select_service)) {
                    spinner_professionals_adapter.clear();
                    professionals_list.add(getString(R.string.select_professional));
                    spinner_professionals_adapter.notifyDataSetChanged();

                    spinner_dates_adapter.clear();
                    dates_list.add(getString(R.string.select_date));
                    spinner_dates_adapter.notifyDataSetChanged();

                    spinner_time_adapter.clear();
                    time_list.add(getString(R.string.select_time));
                    spinner_time_adapter.notifyDataSetChanged();

                    for(int i = 0; i < services_objects.size(); i++) {
                        if(services_objects.get(i).getString("Type").equals(selected_service)){
                            price_textview.setText(services_objects.get(i).getString("Price"), TextView.BufferType.EDITABLE);

                            List<String> professionals = new ArrayList<>();
                            professionals = services_objects.get(i).getList("Professionals");

                            for(i = 0; i < professionals.size(); i++) {
                                spinner_professionals_adapter.add(professionals.get(i));
                            }

                            break;
                        }
                    }

                    spinner_professionals_adapter.notifyDataSetChanged();
                    spinner_professionals.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent_service) {
                Log.d("oh, oh...", "Error");
            }
        });

        spinner_professionals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent_service, View view_service, int position_service, long id_service) {
                String selected_professional = parent_service.getItemAtPosition(position_service).toString();
                if (!selected_professional.equals(R.string.select_professional)) {
                    spinner_dates_adapter.clear();
                    dates_list.add(getString(R.string.select_date));
                    spinner_dates_adapter.notifyDataSetChanged();

                    spinner_time_adapter.clear();
                    time_list.add(getString(R.string.select_time));
                    spinner_time_adapter.notifyDataSetChanged();

                    ParseQuery<ParseObject> query_professional_schedule = ParseQuery.getQuery("Professionals_Schedule");
                    query_professional_schedule.whereEqualTo("Name", selected_professional);
                    query_professional_schedule.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> professionals_schedule_info, ParseException e) {
                            if (e == null && professionals_schedule_info.size() > 0) {
                                professionals_schedule_objects.addAll(professionals_schedule_info);

                                for(int j = 0; j < professionals_schedule_info.size(); j++){
                                    spinner_dates_adapter.add(professionals_schedule_info.get(j).get("Date").toString());
                                }

                                spinner_dates_adapter.notifyDataSetChanged();
                                spinner_dates.setSelection(0);
                            } else {
                                Log.d(":(", "error");
                            }
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent_service) {
                Log.d("oh, oh...", "Error");
            }
        });

        spinner_dates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent_service, View view_service, int position_service, long id_service) {
                String selected_date = parent_service.getItemAtPosition(position_service).toString();
                if (!selected_date.equals(R.string.select_date)) {
                    spinner_time_adapter.clear();
                    time_list.add(getString(R.string.select_time));
                    spinner_time_adapter.notifyDataSetChanged();

                    new_list.clear();

                    for(int i = 0; i < professionals_schedule_objects.size(); i++) {
                        if(professionals_schedule_objects.get(i).getString("Date").equals(selected_date)){

                            alreadyScheduled = professionals_schedule_objects.get(i);
                            if(alreadyScheduled.getList("Already_Scheduled") != null)
                                new_list = alreadyScheduled.getList("Already_Scheduled");

                            List<String> times = new ArrayList<>();
                            times = professionals_schedule_objects.get(i).getList("Times");
                            boolean scheduled;


                            if(new_list.size() > 0) {
                                for (i = 0; i < times.size(); i++) {
                                    scheduled = false;
                                    for (int j = 0; j < new_list.size(); j++)
                                        if (times.get(i).equals(new_list.get(j))) {
                                            scheduled = true;
                                            break;
                                        }

                                    if(!scheduled)
                                        spinner_time_adapter.add(times.get(i));
                                }
                            }
                            else{
                                for (i = 0; i < times.size(); i++) {
                                    spinner_time_adapter.add(times.get(i));
                                }
                            }

                            break;
                        }

                        spinner_time_adapter.notifyDataSetChanged();
                        spinner_time.setSelection(0);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent_service) {
                Log.d("oh, oh...", "Error");
            }
        });

        final Button add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validationError = false;

                int numberOfErros = 0;

                StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.please_select) + " ");
                StringBuilder errors = new StringBuilder("");
                ArrayList error_array = new ArrayList();

                if (spinner_service.getSelectedItem().toString().equals(getString(R.string.select_service))){
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.service));
                }
                if (spinner_professionals.getSelectedItem().toString().equals(getString(R.string.select_professional))){
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.professional));
                }
                if (spinner_dates.getSelectedItem().toString().equals(getString(R.string.select_date))){
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.date));
                }
                if (spinner_time.getSelectedItem().toString().equals(getString(R.string.select_time))){
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.time));
                }


                if (validationError) {

                    if (numberOfErros == 1) {
                        validationErrorMessage.append(getString(R.string.field) + " ");
                    } else {
                        validationErrorMessage.append(getString(R.string.fields) + " ");
                    }

                    for(int i = 1; i <= error_array.size(); i++) {
                        if (i != 1 && i != error_array.size()) {
                            errors.append(", ");
                        }
                        else if (i != 1) {
                            errors.append(" " + getString(R.string.and) + " ");
                        }
                        errors.append(error_array.get(i - 1).toString());
                    }

                    validationErrorMessage.append(errors);
                    validationErrorMessage.append(".");

                    Toast.makeText(SchedulingActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                } else {
                Map<String, String> dimensions = new HashMap<String, String>();
                // Define ranges to bucket data points into meaningful segments
                dimensions.put("service", spinner_service.getSelectedItem().toString());
                dimensions.put("professional", spinner_professionals.getSelectedItem().toString());


                    final ParseObject appointment = new ParseObject("Appointments");
                    appointment.put("Client", ParseUser.getCurrentUser().getUsername());
                    appointment.put("Appointment_Service", spinner_service.getSelectedItem().toString());
                    appointment.put("Appointment_Professional", spinner_professionals.getSelectedItem().toString());
                    appointment.put("Appointment_Date", spinner_dates.getSelectedItem().toString());
                    appointment.put("Appointment_Time", spinner_time.getSelectedItem().toString());
                    ParseAnalytics.trackEventInBackground("new_appointments", dimensions);
                    appointment.saveInBackground();

                    new_list.add(spinner_time.getSelectedItem().toString());
                    alreadyScheduled.put("Already_Scheduled", new_list);
                    alreadyScheduled.saveInBackground();
                    alertDisplayer(getString(R.string.scheduled_appointment), getString(R.string.checkout_appointment), spinner_service.getSelectedItem().toString(), spinner_professionals.getSelectedItem().toString(), spinner_dates.getSelectedItem().toString(), spinner_time.getSelectedItem().toString());
                }
            }
        });
    }

    private void alertDisplayer(String title, String message, final String service, final String professional, final String date, final String time){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SchedulingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_default, null);
        final TextView title_textview = (TextView) mView.findViewById(R.id.title);
        final TextView message_textview = (TextView) mView.findViewById(R.id.message);
        Button mConfirm = (Button) mView.findViewById(R.id.confirm);

        title_textview.setText(title);
        message_textview.setText(message);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchedulingActivity.this, ScheduledActivity.class);
                intent.putExtra("service", service);
                intent.putExtra("professional", professional);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed () {
        Intent intent = new Intent(SchedulingActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
