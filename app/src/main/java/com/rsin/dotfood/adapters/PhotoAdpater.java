package com.rsin.dotfood.adapters;

import android.app.Activity;
import android.content.Context;
import com.rsin.dotfood.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class PhotoAdpater extends BaseAdapter {
    int SELECT_PICTURE = 10;
    ArrayList<Uri> imagesList;
    Context context;


    public PhotoAdpater(Context applicationContext, ArrayList<Uri> imagesList) {
        this.context = applicationContext;
        this.imagesList = imagesList;
    }


    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.addimg_layout, null);
            // set image based on selected text
            ImageView imageView = (ImageView) gridView.findViewById(R.id.addimg);

            imageView.setImageURI(imagesList.get(position));

//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    byte[] bythe = getBytesFromBitmap(imagesList.get(0));
//                    Toast.makeText(context, bythe.toString(), Toast.LENGTH_SHORT).show();
//                }
//            });



        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


//Convert ByteArray to Bitmap:
//    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//    ImageView image = (ImageView) findViewById(R.id.imageView1);
//image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));

}