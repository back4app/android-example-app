package com.example.back4app.barbershopapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class SaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        final ImageView back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final ImageView photo = (ImageView) findViewById(R.id.sale);

        //Setting up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(SaleActivity.this);
        dlg.setTitle(R.string.wait);
        dlg.setMessage(getString(R.string.loading_sales));
        dlg.show();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Sale");
        query.whereExists("Advertisement");
        query.orderByDescending("updatedAt");
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> advertisement, ParseException e) {
                if (e == null && advertisement.size() > 0) {
                    dlg.dismiss();

                    ParseFile imageFile = (ParseFile) advertisement.get(0).getParseFile("Advertisement");

                    if(imageFile != null){
                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                photo.setImageBitmap(bitmap);
                            }
                        });
                    }
                    else{
                        Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.nosale);
                        photo.setImageBitmap(bitmap);

                    }

                } else {
                    dlg.dismiss();
                    Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.nosale);
                    photo.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public void onBackPressed () {
        Intent intent = new Intent(SaleActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
