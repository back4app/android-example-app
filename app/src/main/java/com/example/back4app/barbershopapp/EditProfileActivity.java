package com.example.back4app.barbershopapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordAgainView;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Parse.initialize(this);

        final Button back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        usernameView = (EditText) findViewById(R.id.username);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        passwordAgainView = (EditText) findViewById(R.id.passwordAgain);
        photo = (ImageView) findViewById(R.id.photo);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.nullphoto);
        photo.setImageBitmap(bitmap);

        final String old_email = ParseUser.getCurrentUser().getEmail();
        final String old_username = ParseUser.getCurrentUser().getUsername();

        //Setting up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(EditProfileActivity.this);
        dlg.setTitle(R.string.wait);
        dlg.setMessage(getString(R.string.loading_profile));
        dlg.show();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e) {
                if (e == null && user != null) {
                    ParseFile imageFile = (ParseFile) user.get(0).getParseFile("Photo");

                    if(e == null && imageFile != null){
                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                photo.setImageBitmap(bitmap);
                            }
                        });
                    }
                    else{
                        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.nullphoto);
                        photo.setImageBitmap(bitmap);

                    }
                    usernameView.setText(ParseUser.getCurrentUser().getUsername());
                    emailView.setText(ParseUser.getCurrentUser().getEmail());
                    dlg.dismiss();

                } else {
                    dlg.dismiss();
                    Log.d(":(", "error");
                    Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.nullphoto);
                    photo.setImageBitmap(bitmap);
                }
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), 1);
            }
        });

        final Button edit_button = findViewById(R.id.button);
        edit_button.setText(getString(R.string.edit));
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating the log in data
                boolean validationError = false;

                int numberOfErros = 0;

                ArrayList error_array = new ArrayList();
                StringBuilder validationErrorMessage = new StringBuilder("");

                if (isEmpty(usernameView)) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.username));
                }
                if (isEmpty(emailView)) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.email));
                }
                if (isEmpty(passwordView)) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.password));
                }
                if (isEmpty(passwordAgainView)) {
                    numberOfErros++;
                    validationError = true;
                    error_array.add(getString(R.string.password_again));
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

                }

                if(!validationError) {
                    if (!isMatching(passwordView, passwordAgainView)) {
                        validationError = true;
                        validationErrorMessage.append(getString(R.string.not_matching_passwords));
                    }
                }

                if (validationError) {
                    Toast.makeText(EditProfileActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                //Setting up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(EditProfileActivity.this);
                dlg.setTitle(R.string.wait);
                dlg.setMessage(getString(R.string.editing));
                dlg.show();

                try {
                    // Reset errors
                    emailView.setError(null);
                    passwordView.setError(null);

                    Bitmap bitmapImage = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, stream);

                    byte[] byteArray = stream.toByteArray();

                    final ParseFile file = new ParseFile(usernameView.getText().toString() +".png", byteArray);
                    file.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            // If successful add file to user and signUpInBackground
                            if(null == e){
                                // Sign up with Parse
                                ParseUser user = ParseUser.getCurrentUser();
                                if(!old_username.equals(usernameView.getText().toString())) {
                                    user.setUsername(usernameView.getText().toString());
                                }

                                if(!old_email.equals(emailView.getText().toString())) {
                                    user.setEmail(emailView.getText().toString());
                                }

                                user.setPassword(passwordView.getText().toString());
                                user.put("Photo", file);

                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        dlg.dismiss();
                                        if (e == null) {
                                            if(!old_username.equals(usernameView.getText().toString())) {
                                                ParseQuery<ParseObject> query_appointments = ParseQuery.getQuery("Appointments");
                                                query_appointments.whereEqualTo("Client", old_username);
                                                query_appointments.findInBackground(new FindCallback<ParseObject>() {
                                                    public void done(List<ParseObject> appointments, ParseException e) {
                                                        if (e == null && appointments.size() > 0) {
                                                            for (int i = 0; i < appointments.size(); i++) {
                                                                appointments.get(i).put("Client", usernameView.getText().toString());
                                                                appointments.get(i).saveInBackground();
                                                            }
                                                        } else {
                                                            Log.d(":(", "error");
                                                        }
                                                    }
                                                });

                                                ParseQuery<ParseObject> query_visits = ParseQuery.getQuery("Visits");
                                                query_visits.whereEqualTo("Client", old_username);
                                                query_visits.findInBackground(new FindCallback<ParseObject>() {
                                                    public void done(List<ParseObject> visits, ParseException e) {
                                                        if (e == null && visits.size() > 0) {
                                                            for (int i = 0; i < visits.size(); i++) {
                                                                visits.get(i).put("Client", usernameView.getText().toString());
                                                                visits.get(i).saveInBackground();
                                                            }
                                                        } else {
                                                            Log.d(":(", "error");
                                                        }
                                                    }
                                                });

                                                ParseQuery<ParseObject> query_messages = ParseQuery.getQuery("Contact_Us");
                                                query_messages.whereEqualTo("Username", old_username);
                                                query_messages.findInBackground(new FindCallback<ParseObject>() {
                                                    public void done(List<ParseObject> messages, ParseException e) {
                                                        if (e == null && messages.size() > 0) {
                                                            for (int i = 0; i < messages.size(); i++) {
                                                                messages.get(i).put("Username", usernameView.getText().toString());
                                                                messages.get(i).saveInBackground();
                                                            }
                                                        } else {
                                                            Log.d(":(", "error");
                                                        }
                                                    }
                                                });
                                            }

                                            if(!old_email.equals(emailView.getText().toString())) {
                                                alertDisplayer(getString(R.string.edit_profile_finished), getString(R.string.verify_edited_email), false, usernameView.getText().toString());
                                            }
                                            else{
                                                alertDisplayer(getString(R.string.edit_profile_finished), getString(R.string.dont_forget_changes), false, usernameView.getText().toString());
                                            }

                                        } else {
                                            alertDisplayer(getString(R.string.sorry), getString(R.string.message_unsuccessful_edition), true, usernameView.getText().toString());
                                        }
                                    }
                                });
                            }
                            else{
                                alertDisplayer(getString(R.string.sorry), getString(R.string.message_unsuccessful_edition), true, usernameView.getText().toString());
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private boolean isEmpty(EditText text) {
        if (text.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMatching(EditText text1, EditText text2){
        if(text1.getText().toString().equals(text2.getText().toString())){
            return true;
        }
        else{
            return false;
        }
    }

    private void alertDisplayer(String title,String message, final boolean error, final String username){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(!error) {
                            Intent intent = new Intent(EditProfileActivity.this, MenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){
            if(reqCode == 1){
                photo.setImageURI(data.getData());
            }
        }
    }

    @Override
    public void onBackPressed () {
        Intent intent = new Intent(EditProfileActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
