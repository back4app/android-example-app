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
        username_textview.setText(username);

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

        final Button visits_button = rootView.findViewById(R.id.visits_button);
        visits_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VisitsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final Button appointments_button = rootView.findViewById(R.id.appointments_button);
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
