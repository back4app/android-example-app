package com.example.back4app.barbershopapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Tab2Profile extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_profile, container, false);

        String username = ParseUser.getCurrentUser().getUsername();

        final TextView username_textview = (TextView) rootView.findViewById(R.id.username);
        final TextView email_textview = (TextView) rootView.findViewById(R.id.user_email);
        username_textview.setText(username);
        if(ParseUser.getCurrentUser().getEmail() != null)
            email_textview.setText(ParseUser.getCurrentUser().getEmail());

        final ImageView photo = (ImageView)rootView.findViewById(R.id.photo);

        final ImageView edit_button = (ImageView) rootView.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_password, null);
                final EditText mPassword = (EditText) mView.findViewById(R.id.password);
                Button mConfirm = (Button) mView.findViewById(R.id.confirm);
                Button mCancel = (Button) mView.findViewById(R.id.cancel);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mConfirm.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if(!mPassword.getText().toString().isEmpty()){
                            ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), mPassword.getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    if (e == null && parseUser != null) {
                                        Toast.makeText(getActivity(), getString(R.string.edit_profile_successful), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.invalid_password), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                        else{
                            Toast.makeText(getActivity(), getString(R.string.error_empty_password), Toast.LENGTH_LONG).show();
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

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e) {
                if (e == null) {

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

                } else {
                    Log.d(":(", "error");
                    Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.nullphoto);
                    photo.setImageBitmap(bitmap);
                }
            }
        });

        final ImageView history_button = rootView.findViewById(R.id.history);
        history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VisitsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        final ImageView appointments_button = rootView.findViewById(R.id.appointments);
        appointments_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScheduledActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
