package com.example.back4app.barbershopapp;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.LogInCallback;
import com.parse.ParseTwitterUtils;
import com.parse.SaveCallback;

import java.lang.String;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Parse.initialize(this);
        ParseTwitterUtils.initialize("ibtbhbOqvIyEFB1X9Ll2FXJuW", "Ae2RGBNEAHJgX5HhQBRsypQCReYZXaMp9Pn7CaO06zzWoTmZQ1");
        //ParseFacebookUtils.initialize(this);

        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);

        final Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating the log in data
                boolean validationError = false;

                StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.please_insert) + " ");
                if (isEmpty(usernameView)) {
                    validationError = true;
                    validationErrorMessage.append(getString(R.string.an_username));
                }
                if (isEmpty(passwordView)) {
                    if (validationError) {
                        validationErrorMessage.append(" " + getString(R.string.and) + " ");
                    }
                    validationError = true;
                    validationErrorMessage.append(getString(R.string.a_password));
                }
                validationErrorMessage.append(getString(R.string.point));

                if (validationError) {
                    Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                //Setting up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(LoginActivity.this);
                dlg.setTitle(getString(R.string.wait));
                dlg.setMessage(getString(R.string.logging));
                dlg.show();

                // Reset errors
                passwordView.setError(null);
                // Login with Parse
                ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        dlg.dismiss();
                        if (parseUser != null) {
                            if (parseUser.getBoolean("emailVerified")) {
                                alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + usernameView.getText().toString() + "!", false);
                            } else {
                                ParseUser.logOut();
                                alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.verify_email), true);
                            }
                        } else {
                            ParseUser.logOut();
                            alertDisplayer(getString(R.string.unsuccessful_login), e.getMessage() + " " + getString(R.string.sorry_cant_login), true);
                        }
                    }
                });
            }
        });

        final Button twitter_login_button = findViewById(R.id.twitter_button);
        twitter_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Setting up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(LoginActivity.this);
                dlg.setTitle(getString(R.string.wait));
                dlg.setMessage(getString(R.string.logging));
                dlg.show();

                ParseTwitterUtils.logIn(LoginActivity.this, new LogInCallback() {
                    @Override
                    public void done(final ParseUser user, ParseException err) {
                        if (err != null) {
                            dlg.dismiss();
                            ParseUser.logOut();
                            Toast.makeText(LoginActivity.this, "hey", Toast.LENGTH_LONG).show();
                            alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                        }
                        if (user == null) {
                            dlg.dismiss();
                            ParseUser.logOut();
                            Toast.makeText(LoginActivity.this, "hey1", Toast.LENGTH_LONG).show();
                            alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                        } else if (user.isNew()) {
                            dlg.dismiss();

                            user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (null == e) {
                                        alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                                    } else {
                                        ParseUser.logOut();
                                        Toast.makeText(LoginActivity.this, "hey2", Toast.LENGTH_LONG).show();
                                        alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                                    }
                                }
                            });
                        } else {
                            dlg.dismiss();
                            alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                        }
                    }
                });
            }
        });

        /*final Button facebook_button = findViewById(R.id.facebook_button);
        facebook_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collection<String> permissions = Arrays.asList("public_profile", "email");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {

                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (err != null) {
                            ParseUser.logOut();
                            alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                        }
                        if (user == null) {
                            ParseUser.logOut();
                            alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                        } else if (user.isNew()) {
                            getUserDetailFromFB();
                            alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                        } else {
                            alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                        }
                    }
                });
            }
        });*/

        final Button signup_button = findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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

    /*private void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                ParseUser user = ParseUser.getCurrentUser();
                try{
                    user.setUsername(object.getString("name"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                    user.setEmail(object.getString("email"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        alertDisplayer("First Time Login!", "Welcome!", false);
                    }
                });
            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }*/

    private void alertDisplayer(String title, String message, final boolean error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (!error) {
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }*/

}
