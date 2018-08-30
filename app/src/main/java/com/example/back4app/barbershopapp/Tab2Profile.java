package com.example.back4app.barbershopapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
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

        final ImageView visits_icon = rootView.findViewById(R.id.visits_icon);
        final TextView visits_text = rootView.findViewById(R.id.visits_text);

        visits_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VisitsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        visits_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VisitsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final ImageView appointments_icon = rootView.findViewById(R.id.appointments_icon);
        final TextView appointments_text = rootView.findViewById(R.id.appointments_text);

        appointments_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScheduledActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        appointments_text.setOnClickListener(new View.OnClickListener() {
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
