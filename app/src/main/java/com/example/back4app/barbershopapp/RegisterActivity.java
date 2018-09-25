package com.example.back4app.barbershopapp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordAgainView;
    private ImageView photo;
    private ImageView edit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Parse.initialize(this);

        usernameView = (EditText) findViewById(R.id.username);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        passwordAgainView = (EditText) findViewById(R.id.passwordAgain);
        photo = (ImageView) findViewById(R.id.photo);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.nullphoto);
        photo.setImageBitmap(bitmap);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), 1);
            }
        });

        edit_button = (ImageView) findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), 1);
            }
        });

        final Button register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
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
                if (isEmpty(emailView)){
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
                    Toast.makeText(RegisterActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isEmailValid(emailView.getText().toString())){
                    validationErrorMessage.append(getString(R.string.invalid_email));
                    Toast.makeText(RegisterActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                //Setting up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(RegisterActivity.this, R.style.AlertDialogTheme);
                dlg.setTitle(getString(R.string.wait));
                dlg.setMessage(getString(R.string.signing));
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
                                ParseUser user = new ParseUser();
                                user.setUsername(usernameView.getText().toString());
                                user.setPassword(passwordView.getText().toString());
                                user.setEmail(emailView.getText().toString());
                                user.put("Photo", file);

                                user.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        dlg.dismiss();
                                        if (e == null) {
                                            ParseUser.logOut();
                                            alertDisplayer(getString(R.string.message_successful_creation), getString(R.string.please_verify), false);
                                        } else {
                                            ParseUser.logOut();
                                            alertDisplayer(getString(R.string.message_unsuccessful_creation), getString(R.string.not_created) + " :" + e.getMessage(), true);
                                        }
                                    }
                                });
                            }
                            else{
                                alertDisplayer(getString(R.string.message_unsuccessful_creation), getString(R.string.not_created) + " :" + e.getMessage(), true);
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

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isMatching(EditText text1, EditText text2){
        if(text1.getText().toString().equals(text2.getText().toString())){
            return true;
        }
        else{
            return false;
        }
    }

    private void alertDisplayer(String title,String message, final boolean error){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
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
                if (!error) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){
            if(reqCode == 1){
                photo.setImageURI(data.getData());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
