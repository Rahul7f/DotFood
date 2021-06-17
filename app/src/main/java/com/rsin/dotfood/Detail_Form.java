package com.rsin.dotfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.essam.simpleplacepicker.MapActivity;
import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rsin.dotfood.adapters.PhotoAdpater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Detail_Form extends AppCompatActivity {
    String uuid;
    //views//
    MaterialButton choose_location,addimg_btn,savedata_btn;
    ImageView document_imageview,aadhar_back_iv;
    TextInputLayout title_et,description_et,pincode_et,name_et,phone_et,full_address_et;
    Spinner state,city;
    TextView longlat;
    GridView gridLayout;
    String latitude;
    String longitude;
    public  int i;
    //views end//
    PhotoAdpater photoAdpater;

    ArrayList<String> statelist;
    ArrayList<Uri> imagesEncodedList;
    int PICK_IMAGE_MULTIPLE = 0;
    int REQUEST_PLACE_PICKER = 1;
    int PICK_IMAGE_1 = 2;
    int PICK_IMAGE_2 = 3;


    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    Uri imageUri,document_uri;

    //firebase stuff
    FirebaseStorage storage;
    StorageReference storageRef;
//    FirebaseAuth auth;
    List<String> downloded_img_list;
    FirebaseFirestore db;
    CollectionReference helpers_form_collection_reference;
    CollectionReference warriors_form_collection_reference;
    ProgressDialog progressDialog;

    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form);
        uuid = UUID.randomUUID().toString();

        //hooks//
        state = findViewById(R.id.state_spinner);
        city = findViewById(R.id.city_spinner);
        gridLayout = findViewById(R.id.gridlayout);
        addimg_btn = findViewById(R.id.add_imgbtn);
        choose_location = findViewById(R.id.picklocation_btn);
        longlat = findViewById(R.id.longlet);
        progressDialog = new ProgressDialog(Detail_Form.this);
        progressDialog.setTitle("Data uploading please wait");
        progressDialog.setCancelable(false);
        path = getIntent().getStringExtra("tag");
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

        title_et = findViewById(R.id.title_et);
        description_et = findViewById(R.id.description_et);
        pincode_et = findViewById(R.id.pincode);
        full_address_et = findViewById(R.id.full_address_et);
        name_et = findViewById(R.id.name_et);
        phone_et = findViewById(R.id.phone_et);
        document_imageview = findViewById(R.id.aadhar_card_font);
        savedata_btn = findViewById(R.id.post_btn);
        // hookes end//

        // instance//
        statelist = new ArrayList<String>();
        imagesEncodedList = new ArrayList<Uri>();
        Places.initialize(getApplicationContext(), "AIzaSyCvBiSu7l0cfHADT_y0XNPrV2APIarkvTI");
        downloded_img_list = new ArrayList<String>();
        //--end--//

        //firebase stuff
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();

        helpers_form_collection_reference = db.collection("helpers_forms");
        warriors_form_collection_reference = db.collection("warriors_form");

        // Create a reference to "mountains.jpg"
//        StorageReference mountainsRef = storageRef.child("mountains.jpg");

        //--end--//
        textWatcher(title_et);
        textWatcher(description_et);
        textWatcher(pincode_et);
        textWatcher(full_address_et);
        textWatcher(name_et);
        textWatcher(phone_et);


        addimg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            }
        });

        choose_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlacePicker();
            }
        });

        document_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_1);
            }
        });

//        aadhar_back_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_2);
//            }
//        });

        savedata_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedata();

            }
        });


        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            Iterator<String> iter = obj.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = obj.get(key);

                    statelist.add(key);

                } catch (JSONException e) {
                    // Something went wrong!
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statelist);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(arrayAdapter);

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getSelectedItem().toString();
                try {
                    JSONObject obj = new JSONObject(loadJSONFromAsset());
                    JSONArray jsonArray = obj.getJSONArray(selected);
                    ArrayList<String> citylist =citylist = new ArrayList<String>();

                    for(int i=0; i<jsonArray.length(); i++) {
                        citylist.add(jsonArray.getString(i));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, citylist);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    city.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


    }

    private void savedata() {
        String title,description,pincode,full_address,name,phone;
        title = title_et.getEditText().getText().toString();
        description = description_et.getEditText().getText().toString();
        pincode = pincode_et.getEditText().getText().toString();
        full_address = full_address_et.getEditText().getText().toString();
        name = name_et.getEditText().getText().toString();
        phone = phone_et.getEditText().getText().toString();

        if (isempity_et(title,description,pincode,full_address,name,phone)){

            // image listvalidation
            if (imagesEncodedList.isEmpty())
            {
                Toast.makeText(Detail_Form.this, "choose some image", Toast.LENGTH_SHORT).show();

            }
            //latitude and longitude validation
            else if (latitude==null && longitude==null){
                Toast.makeText(Detail_Form.this, "pick location", Toast.LENGTH_SHORT).show();
                choose_location.requestFocus();
            }
            // aadhar card validation
            else if (document_uri==null)
            {
                Toast.makeText(Detail_Form.this, "choose document", Toast.LENGTH_SHORT).show();
                document_imageview.requestFocus();
            }
            //state spinner validation
            else if (state.getSelectedItem().toString().equals("Choose State")){
                TextView errorText = (TextView)state.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("choose state");//changes the selected item text to this
                errorText.requestFocus();
//                Toast.makeText(this, "spinner state", Toast.LENGTH_SHORT).show();

            }

            //city spinner validation
            else if (city.getSelectedItem().toString().equals("choose district")){
                TextView errorText = (TextView)city.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("choose city");//changes the selected item text to this
                errorText.requestFocus();
//                Toast.makeText(this, "spinner city", Toast.LENGTH_SHORT).show();

            }

            else {
                progressDialog.show();
                List<String> downloaded_img_url = new ArrayList<>();
                Toast.makeText(this, ""+imagesEncodedList.size(), Toast.LENGTH_SHORT).show();

                for(i=0; i<imagesEncodedList.size(); i++) {
                    Uri uri = imagesEncodedList.get(i);
                    String uuid2 =UUID.randomUUID().toString();

                    StorageReference imgref = storageRef.child("image/"+uuid+"/"+uuid2+".jpg");

                    UploadTask uploadTask =  imgref.putFile(uri);
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return imgref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful())
                            {
//                    upload done
                                Uri downloadUri = task.getResult();
                                downloaded_img_url.add(downloadUri.toString());
//                        Log.d("img_url",downloadUri.toString()+" i "+i);
                                Log.e("I +uris size",i+" "+downloaded_img_url.size());
                                //oncomple ke phele chcek kr
                                if (i==downloaded_img_url.size())
                                {
                                    String uuid = UUID.randomUUID().toString();
                                    String doc_img_url =  upload_single_images(document_uri);
                                    String state_v,category_v,city_v;
                                    state_v = state.getSelectedItem().toString();
                                    city_v = city.getSelectedItem().toString();
                                    DataModel been = new DataModel(title,description,state_v,city_v,pincode,full_address,latitude,longitude,name,uuid,phone,downloaded_img_url,doc_img_url);

                                    db.collection(path).document(uuid).set(been)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(Detail_Form.this,HomeActivity.class));
                                                    finish();
                                                    Toast.makeText(Detail_Form.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }

                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(Detail_Form.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        }

    }

    private boolean isempity_et(String title, String description, String pincode, String full_address, String name, String phone ) {
        if (title.isEmpty())
        {
            title_et.setError("enter title");
            title_et.requestFocus();

        }
        else if (description.isEmpty())
        {
            description_et.setError("enter description");
            description_et.requestFocus();
        }
        else if (pincode.isEmpty())
        {
            pincode_et.setError("enter pincode");
            pincode_et.requestFocus();
        }
        else if (full_address.isEmpty())
        {
            full_address_et.setError("enter address");
            full_address_et.requestFocus();
        }
        else if (name.isEmpty())
        {
            name_et.setError("enter name");
            name_et.requestFocus();
        }
        else if (phone.isEmpty())
        {
            phone_et.setError("enter phone");
            phone_et.requestFocus();
        }
        else {
            return true;
        }

        return false;
    }

    void textWatcher(TextInputLayout textInputLayout){
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textInputLayout.setError(null);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 0:
                try {
                    if(requestCode == PICK_IMAGE_MULTIPLE) {
                        if(resultCode == RESULT_OK) {
                            if(data.getClipData() != null) {
                                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                                if (count>10)
                                {
                                    Toast.makeText(this, "max 10 image are allow", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    for(int i = 0; i < count; i++)
                                    {
                                        imageUri = data.getClipData().getItemAt(i).getUri();
                                        imagesEncodedList.add(imageUri);

                                    }
                                }

                                photoAdpater = new PhotoAdpater(getApplicationContext(),imagesEncodedList);
                                gridLayout.setAdapter(photoAdpater);
                                gridLayout.setVisibility(View.VISIBLE);

                                //do something with the image (save it to some directory or whatever you need to do with it here)
                            }
                        } else if(data.getData() != null) {
                            String imagePath = data.getData().getPath();
                            //do something with the image (save it to some directory or whatever you need to do with it here)
                        }
                        else {
                            Toast.makeText(this, "You haven't picked Image null data",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "You haven't picked Image "+e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }

                break;
            case 1:
                if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == RESULT_OK)) {
                    if (data!=null)
                    {
                        StringBuilder stringBuilder = new StringBuilder();
                        latitude= String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA,-1));
                        longitude= String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA,-1));
                        stringBuilder.append("Latitude: ");
                        stringBuilder.append(latitude);
                        stringBuilder.append("\n");
                        stringBuilder.append("longitude: ");
                        stringBuilder.append(longitude);
                        longlat.setText(stringBuilder.toString());
                    }
//                    Place place = PingPlacePicker.getPlace(data);
//                    Toast.makeText(this, data.getData().toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("mapdata",data.getData().toString());
//                    if (place != null) {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        latitude= String.valueOf(place.getLatLng().latitude);
//                        longitude= String.valueOf(place.getLatLng().longitude);
//                        stringBuilder.append("Latitude: ");
//                        stringBuilder.append(latitude);
//                        stringBuilder.append("\n");
//                        stringBuilder.append("longitude: ");
//                        stringBuilder.append(longitude);
//                        longlat.setText(stringBuilder.toString());
//                    }
                }
                break;
            case 2:
                if (requestCode == PICK_IMAGE_1) {
                    //TODO: action
                    if (data!=null)
                    {
                        document_uri = data.getData();
                        document_imageview.setImageURI(document_uri);

                    }
                    else {
                        Toast.makeText(this, "u have not choose any image", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case 3:
                if (requestCode == PICK_IMAGE_2) {
                    //TODO: action

                }
                break;
        }


    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void showPlacePicker() {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(SimplePlacePicker.API_KEY,"AIzaSyCvBiSu7l0cfHADT_y0XNPrV2APIarkvTI");
//        bundle.putString(SimplePlacePicker.COUNTRY,country);
//        bundle.putString(SimplePlacePicker.LANGUAGE,language);
//        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,supportedAreas);

        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_PLACE_PICKER);
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(Detail_Form.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(Detail_Form.this, new String[] { permission }, requestCode);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            Toast.makeText(Detail_Form.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Detail_Form.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(Detail_Form.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Detail_Form.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Detail_Form.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void upload_images_from_list(ArrayList<Uri> uris)
    {
        progressDialog.show();
        List<String> downloaded_img_url = new ArrayList<>();
        Toast.makeText(this, ""+uris.size(), Toast.LENGTH_SHORT).show();

        for(i=0; i<uris.size(); i++) {
            Uri uri = uris.get(i);
            String uuid2 = UUID.randomUUID().toString();
            StorageReference imgref = storageRef.child("image/"+uuid+"/"+uuid2+".jpg");

            UploadTask uploadTask =  imgref.putFile(uri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imgref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
//                    upload done
                        Uri downloadUri = task.getResult();
                        downloaded_img_url.add(downloadUri.toString());
//                        Log.d("img_url",downloadUri.toString()+" i "+i);
                        Log.e("I +uris size",i+" "+downloaded_img_url.size());
                        //oncomple ke phele chcek kr
                        if (i==downloaded_img_url.size())
                        {
                            progressDialog.dismiss();
                        }

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(Detail_Form.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
    String upload_single_images(Uri uri)
    {
        final String[] downloadUri = new String[1];
        StorageReference imgref = storageRef.child("aadhar/"+uuid+".jpg");
        UploadTask uploadTask =  imgref.putFile(uri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imgref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                {
//                    upload done
                    downloadUri[0] = task.getResult().toString();
                    Log.d("myurl", downloadUri[0].toString());
                    Toast.makeText(Detail_Form.this, "sucessfully uplaod\n", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Detail_Form.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        return downloadUri[0];


    }
}