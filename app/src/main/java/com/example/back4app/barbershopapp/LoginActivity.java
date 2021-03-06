package com.example.back4app.barbershopapp;

import android.graphics.Color;
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
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
//import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.LogInCallback;
//import com.parse.ParseTwitterUtils;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.twitter.ParseTwitterUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.String;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                final ProgressDialog dlg = new ProgressDialog(LoginActivity.this, R.style.AlertDialogTheme);
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

        final ImageView facebook_button = findViewById(R.id.facebook_button);
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
                        else if (user == null) {
                            ParseUser.logOut();
                            alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                        } else if (user.isNew()) {
                            getUserDetailFromFB();
                        } else {
                            alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                        }
                    }
                });
            }
        });

        final ImageView twitter_button = findViewById(R.id.twitter_button);
        twitter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseTwitterUtils.logIn(LoginActivity.this, new LogInCallback() {

                    @Override
                    public void done(final ParseUser user, ParseException err) {
                        if (err != null) {
                            ParseUser.logOut();
                            alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                        }
                        else if (user == null) {
                            ParseUser.logOut();
                            alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                        } else if (user.isNew()) {
                            user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (null == e) {
                                        alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                                    } else {
                                        ParseUser.logOut();
                                        alertDisplayer(getString(R.string.unsuccessful_login), getString(R.string.sorry_cant_login), true);
                                    }
                                }
                            });
                        } else {
                            alertDisplayer(getString(R.string.successful_login), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                        }
                    }
                });

            }
        });

        final TextView reset_password = findViewById(R.id.reset_password);
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
                final EditText mEmail = (EditText) mView.findViewById(R.id.email);
                Button mConfirm = (Button) mView.findViewById(R.id.confirm);
                Button mCancel = (Button) mView.findViewById(R.id.cancel);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mConfirm.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        if(!isEmailValid(mEmail.getText().toString())){
                            Toast.makeText(LoginActivity.this, getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(!mEmail.getText().toString().isEmpty()){
                            final String email = mEmail.getText().toString();
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo("email", email);
                            query.setLimit(1);
                            query.findInBackground(new FindCallback<ParseUser>() {
                                public void done(List<ParseUser> user, ParseException e) {
                                    if (e == null && user.size() > 0) {
                                        // The query was successful.

                                        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    // An email was successfully sent with reset instructions.
                                                    Toast.makeText(LoginActivity.this, getString(R.string.reset_password_sucessful), Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(LoginActivity.this, getString(R.string.reset_password_unsucessful), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } else {
                                        // Something went wrong.
                                        Toast.makeText(LoginActivity.this, getString(R.string.error_user_not_found), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                        else{
                            Toast.makeText(LoginActivity.this, getString(R.string.error_empty_email), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                mCancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    private boolean isEmpty(EditText text) {
        if (text.getText().toString().trim().length() > 0)
            return false;

        return true;

    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void getUserDetailFromFB(){
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
                        alertDisplayer(getString(R.string.message_successful_creation), getString(R.string.welcome) + " " + ParseUser.getCurrentUser().get("username").toString() + "!", false);
                    }
                });
            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void alertDisplayer(String title, String message, final boolean error) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
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
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                dialog.cancel();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
