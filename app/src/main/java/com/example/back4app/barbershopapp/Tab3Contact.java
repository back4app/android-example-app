package com.example.back4app.barbershopapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class Tab3Contact extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_contact, container, false);

        Parse.initialize(getActivity());

        final EditText name = (EditText) rootView.findViewById(R.id.name_input);
        final EditText email = (EditText) rootView.findViewById(R.id.email_input);
        final EditText message = (EditText) rootView.findViewById(R.id.message_input);
        final Spinner spinner_subject = (Spinner) rootView.findViewById(R.id.subject_dropdown);

        ArrayAdapter<CharSequence> adapter_subject = ArrayAdapter.createFromResource(getActivity(), R.array.array_dropdown_subject, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_subject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner_subject.setAdapter(adapter_subject);

        final Button send_button = rootView.findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating the log in data
                boolean validationError = false;

                int numberOfErros = 0;

                ArrayList error_array = new ArrayList();
                StringBuilder validationErrorMessage = new StringBuilder("");

                if (isEmpty(name)) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.name));
                }
                if (isEmpty(email)) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.email));
                }
                if (spinner_subject.getSelectedItem().toString().equals(getString(R.string.select_subject))) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.subject));
                }
                if (isEmpty(message)) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.message));
                }

                if (validationError) {

                    validationErrorMessage.append(getString(R.string.please_complete) + " ");

                    StringBuilder errors = new StringBuilder("");

                    if (numberOfErros == 1) {
                        validationErrorMessage.append(getString(R.string.required_field) + " ");
                    } else {
                        validationErrorMessage.append(getString(R.string.required_fields) + " ");
                    }

                    for (int i = 1; i <= error_array.size(); i++) {
                        if (i != 1 && i != error_array.size()) {
                            errors.append(", ");
                        } else if (i != 1) {
                            errors.append(" " + getString(R.string.and) + " ");
                        }
                        errors.append(error_array.get(i - 1).toString());
                    }

                    validationErrorMessage.append(errors);
                    validationErrorMessage.append(".");

                    Toast.makeText(getActivity(), validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isEmailValid(email.getText().toString())){
                    validationErrorMessage.append(getString(R.string.invalid_email));
                    Toast.makeText(getActivity(), validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userDoubt = message.getText().toString();

                //Setting up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setTitle(getString(R.string.wait));
                dlg.setMessage(getString(R.string.sending));
                dlg.show();


                final ParseObject message_client = new ParseObject("Contact_Us");
                message_client.put("Name", name.getText().toString());
                message_client.put("Username", ParseUser.getCurrentUser().get("username").toString());
                message_client.put("Email", email.getText().toString());
                message_client.put("Subject", spinner_subject.getSelectedItem().toString());
                message_client.put("Message", message.getText().toString());

                dlg.dismiss();

                message_client.saveInBackground();

                alertDisplayer(getString(R.string.thanks), getString(R.string.soon));
            }
        });

        return rootView;
    }

    private boolean isEmpty(EditText text) {
        if (text.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(getActivity(), MenuActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}