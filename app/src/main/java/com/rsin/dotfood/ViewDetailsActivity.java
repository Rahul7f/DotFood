package com.rsin.dotfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rsin.dotfood.adapters.SliderAdapter;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class ViewDetailsActivity extends AppCompatActivity {
    private static final int REQUEST_PHONE_CALL = 1;
    String latitude,longitude,phone_no;
    TextView title,description,address,phone,name;
    Button getlocation,calling_bnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
        SliderView sliderView = findViewById(R.id.slide_img);
        getlocation  = findViewById(R.id.get_location);
        title = findViewById(R.id.title_d);
        description = findViewById(R.id.description_d);
        address = findViewById(R.id.address_d);
        phone = findViewById(R.id.phone_d);
        name = findViewById(R.id.name_d);
        calling_bnt = findViewById(R.id.calling_btn);
        DataModel formBeen = (DataModel) getIntent().getSerializableExtra("object");
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        for (int i = 0; i<formBeen.getAll_photos().size();i++)
        {
            sliderDataArrayList.add(new SliderData(formBeen.getAll_photos().get(i)));
        }

        SliderAdapter adapter = new SliderAdapter(ViewDetailsActivity.this, sliderDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        latitude = formBeen.getLatitude();
        longitude = formBeen.getLongitude();
        phone_no = formBeen.getPhone();
        title.setText(formBeen.getTitle());
        description.setText(formBeen.getDescription());
        name.setText(formBeen.getName());
        phone.setText(formBeen.getPhone());
        address.setText(formBeen.getAddress());

        calling_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                    my_callIntent.setData(Uri.parse("tel:"+phone_no));
                    //here the word 'tel' is important for making a call...
                    startActivity(my_callIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Label which you want" + ")";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                if (ContextCompat.checkSelfPermission(ViewDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ViewDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(intent);
                }


            }
        });

    }
}