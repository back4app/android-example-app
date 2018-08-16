package com.example.back4app.barbershopapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduledActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled);

        final TextView services_textview = (TextView) findViewById(R.id.services_textview);
        final TextView professionals_textview = (TextView) findViewById(R.id.professionals_textview);
        final TextView dates_textview = (TextView) findViewById(R.id.dates_textview);
        final TextView time_textview = (TextView) findViewById(R.id.time_textview);

        /*services = new ArrayList<>();
        services.add("Outras Liberações");
        final Spinner spinner_register = (Spinner) findViewById(R.id.liberacoes_dropdown);
        final ArrayAdapter<String> spinner_adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_register.setAdapter(spinner_adapter);
        spinner_register.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (!selected.equals("Outras Liberações") && selected != parent.getItemAtPosition(0)) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Liberados");
                    query.whereEqualTo("Nome", selected);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> liberado, ParseException e) {
                            if (e == null) {
                                type_liberation_textView.setText(liberado.get(0).getString("Tipo_Liberacao"), TextView.BufferType.EDITABLE);

                                name_textView.setText(liberado.get(0).getString("Nome"), TextView.BufferType.EDITABLE);

                                arrival_textView.setText(liberado.get(0).getString("Destino"), TextView.BufferType.EDITABLE);

                                complement_textView.setText(liberado.get(0).getString("Complemento"), TextView.BufferType.EDITABLE);

                                type_document_textView.setText(liberado.get(0).getString("Tipo_Documento"), TextView.BufferType.EDITABLE);

                                document_textView.setText(liberado.get(0).getString("Documento"), TextView.BufferType.EDITABLE);


                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
                parent.setSelection(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("FUNFA", "WE ARE DOOMED");
            }
        });*/

    }
}
